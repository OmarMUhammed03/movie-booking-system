package com.moviebooking.payment_service.service.impl;

import com.moviebooking.payment_service.config.StripeProperties;
import com.moviebooking.payment_service.dto.CheckoutSessionDetails;
import com.moviebooking.payment_service.dto.StripeCheckoutLineItem;
import com.moviebooking.payment_service.dto.request.CreateCheckoutSessionRequest;
import com.moviebooking.payment_service.dto.response.CheckoutSessionResponse;
import com.moviebooking.payment_service.exception.PaymentAlreadyExistsException;
import com.moviebooking.payment_service.exception.PaymentNotFoundException;
import com.moviebooking.payment_service.model.Payment;
import com.moviebooking.payment_service.model.PaymentStatus;
import com.moviebooking.payment_service.repository.PaymentRepository;
import com.moviebooking.payment_service.service.CheckoutDetailsService;
import com.moviebooking.payment_service.service.PaymentService;
import com.moviebooking.payment_service.service.StripeService;
import com.stripe.model.checkout.Session;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final StripeService stripeService;
    private final CheckoutDetailsService checkoutDetailsService;
    private final StripeProperties stripeProperties;

    @Override
    @Transactional
    public CheckoutSessionResponse createCheckoutSession(CreateCheckoutSessionRequest request) {
        long amountCents = toAmountCents(request.totalPrice());
        String currency = stripeProperties.getCurrency();

        Payment payment = paymentRepository.findByReservationId(request.reservationId());

        if (payment == null) {
            payment = Payment.builder()
                    .reservationId(request.reservationId())
                    .amountCents(amountCents)
                    .currency(currency)
                    .status(PaymentStatus.PENDING)
                    .build();

            payment = paymentRepository.save(payment);
        } else if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new PaymentAlreadyExistsException(request.reservationId());
        }

        CheckoutSessionDetails details = checkoutDetailsService.resolve(request.showId(), request.ticketIds());

        Map<String, String> metadata = Map.of(
                "paymentId", payment.getId().toString(),
                "reservationId", request.reservationId().toString(),
                "userId", request.userId().toString(),
                "showId", request.showId().toString()
        );

        StripeCheckoutLineItem lineItem = new StripeCheckoutLineItem(
                amountCents,
                currency,
                details.productName(),
                details.description(),
                details.posterUrl()
        );

        Session session = stripeService.createCheckoutSession(lineItem, metadata);

        payment.setStripeSessionId(session.getId());
        Payment updatedPayment = paymentRepository.save(payment);

        return new CheckoutSessionResponse(
                updatedPayment.getId(),
                updatedPayment.getReservationId(),
                updatedPayment.getStripeSessionId(),
                session.getUrl(),
                updatedPayment.getAmountCents(),
                updatedPayment.getCurrency(),
                updatedPayment.getStatus()
        );
    }

    @Override
    @Transactional
    public void handleWebhookEvent(String payload, String signatureHeader) {

        log.info( "payload: {} \n signatureHeader: {}" , payload , signatureHeader);

        Event event = stripeService.constructWebhookEvent(payload, signatureHeader);

        StripeObject stripeObject = event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> new IllegalStateException("Unable to deserialize Stripe event"));

        log.info( "event type {}" , event.getType());

        switch (event.getType()) {
            case "checkout.session.completed" -> {
                if (stripeObject instanceof Session session) {

                    log.info( "Session {}" , session);
                    handleCheckoutSessionCompleted(session);
                }
            }
            case "checkout.session.expired" -> {
                if (stripeObject instanceof Session session) {
                    updatePaymentStatus(session.getId(), PaymentStatus.FAILED);
                }
            }
            default -> log.debug("Unhandled Stripe event type: {}", event.getType());
        }
    }

    private void handleCheckoutSessionCompleted(Session session) {
        if (!"paid".equals(session.getPaymentStatus())) {
            log.warn("Checkout session {} completed with payment status: {}",
                    session.getId(), session.getPaymentStatus());
            return;
        }
        updatePaymentStatus(session.getId(), PaymentStatus.SUCCEEDED);
    }

    private void updatePaymentStatus(String stripeSessionId, PaymentStatus status) {
        Payment payment = paymentRepository.findByStripeSessionId(stripeSessionId);
        if (payment == null) {
            throw new PaymentNotFoundException("Payment not found for Stripe session: " + stripeSessionId);
        }

        if (payment.getStatus() == status) {
            log.info("Payment {} is already {}", payment.getId(), status);
            return;
        }

        payment.setStatus(status);
        paymentRepository.save(payment);
        log.info("Updated payment {} to status {} via webhook", payment.getId(), status);
    }

    private long toAmountCents(BigDecimal totalPrice) {
        return totalPrice.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }
}
