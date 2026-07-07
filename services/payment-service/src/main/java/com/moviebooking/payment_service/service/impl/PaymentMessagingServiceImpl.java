package com.moviebooking.payment_service.service.impl;

import com.moviebooking.payment_service.config.RabbitMQConfig;
import com.moviebooking.payment_service.model.Payment;
import com.moviebooking.payment_service.service.PaymentMessagingService;
import com.moviebooking.shared.event.ReservationSagaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentMessagingServiceImpl implements PaymentMessagingService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishPaymentSucceeded(Payment payment) {
        ReservationSagaEvent event = toSagaEvent(payment, null);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_PAYMENT_SUCCEEDED,
                event
        );
        log.info("Published payment.succeeded for reservation {}", payment.getReservationId());
    }

    @Override
    public void publishPaymentFailed(Payment payment, String reason) {
        ReservationSagaEvent event = toSagaEvent(payment, reason);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_PAYMENT_FAILED,
                event
        );
        log.info("Published payment.failed for reservation {}", payment.getReservationId());
    }

    private ReservationSagaEvent toSagaEvent(Payment payment, String reason) {
        return ReservationSagaEvent.builder()
                .reservationId(payment.getReservationId())
                .userId(payment.getUserId())
                .showId(payment.getShowId())
                .ticketIds(payment.getTicketIds())
                .totalPrice(payment.getTotalPrice())
                .reason(reason)
                .build();
    }
}
