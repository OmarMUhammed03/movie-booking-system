package com.moviebooking.reservation_service.messaging;

import com.moviebooking.reservation_service.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReservationCreatedEventListener {

    private final RabbitTemplate rabbitTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onReservationCreated(ReservationCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.RESERVATION_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_RESERVATION_CREATED,
                event.sagaEvent()
        );
    }
}
