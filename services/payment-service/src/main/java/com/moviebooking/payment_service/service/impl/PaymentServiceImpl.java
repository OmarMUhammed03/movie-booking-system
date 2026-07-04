package com.moviebooking.payment_service.service.impl;

import com.moviebooking.payment_service.dto.request.CreateCheckoutSessionRequest;
import com.moviebooking.payment_service.dto.response.CheckoutSessionResponse;
import com.moviebooking.payment_service.exception.PaymentAlreadyExistsException;
import com.moviebooking.payment_service.model.Payment;
import com.moviebooking.payment_service.model.PaymentStatus;
import com.moviebooking.payment_service.repository.PaymentRepository;
import com.moviebooking.payment_service.service.PaymentService;
import com.moviebooking.payment_service.service.StripeService;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final StripeService stripeService;

    @Override
    @Transactional
    public CheckoutSessionResponse createCheckoutSession(CreateCheckoutSessionRequest request) {

        Payment payment = paymentRepository.findByReservationId(request.reservationId());

        if (payment == null) {
            payment = Payment.builder()
                    .reservationId(request.reservationId())
                    .amountCents(request.amountCents())
                    .currency(request.currency())
                    .status(PaymentStatus.PENDING)
                    .build();

            payment = paymentRepository.save(payment);
        } else if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new PaymentAlreadyExistsException(request.reservationId());
        }

        Map<String, String> metadata = Map.of(
                "paymentId", payment.getId().toString(),
                "reservationId", request.reservationId().toString()
        );

        Session session = stripeService.createCheckoutSession(
                request.amountCents(),
                request.currency(),
                metadata
        );

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
}
