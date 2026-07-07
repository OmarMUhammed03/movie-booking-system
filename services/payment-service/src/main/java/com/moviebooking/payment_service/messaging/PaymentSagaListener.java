package com.moviebooking.payment_service.messaging;

import com.moviebooking.payment_service.config.RabbitMQConfig;
import com.moviebooking.payment_service.config.StripeProperties;
import com.moviebooking.payment_service.model.Payment;
import com.moviebooking.payment_service.model.PaymentStatus;
import com.moviebooking.payment_service.repository.PaymentRepository;
import com.moviebooking.shared.event.ReservationSagaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSagaListener {

    private final PaymentRepository paymentRepository;
    private final StripeProperties stripeProperties;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_PROCESS_QUEUE)
    @Transactional
    public void onTicketReserved(ReservationSagaEvent event) {
        log.info("Received ticket.reserved for reservation {}", event.getReservationId());

        Payment payment = paymentRepository.findByReservationId(event.getReservationId());
        if (payment != null && payment.getStatus() != PaymentStatus.PENDING) {
            log.info("Payment for reservation {} already resolved, ignoring ticket.reserved", event.getReservationId());
            return;
        }

        long amountCents = toAmountCents(event.getTotalPrice());

        if (payment == null) {
            payment = Payment.builder()
                    .reservationId(event.getReservationId())
                    .userId(event.getUserId())
                    .showId(event.getShowId())
                    .ticketIds(event.getTicketIds())
                    .totalPrice(event.getTotalPrice())
                    .amountCents(amountCents)
                    .currency(stripeProperties.getCurrency())
                    .status(PaymentStatus.PENDING)
                    .build();
        } else {
            payment.setUserId(event.getUserId());
            payment.setShowId(event.getShowId());
            payment.setTicketIds(event.getTicketIds());
            payment.setTotalPrice(event.getTotalPrice());
            payment.setAmountCents(amountCents);
        }

        paymentRepository.save(payment);
        log.info("Payment record ready for reservation {}", event.getReservationId());
    }

    private long toAmountCents(BigDecimal totalPrice) {
        if (totalPrice == null) {
            return 0L;
        }
        return totalPrice.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }
}
