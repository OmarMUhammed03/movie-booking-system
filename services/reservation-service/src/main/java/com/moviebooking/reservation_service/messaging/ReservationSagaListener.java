package com.moviebooking.reservation_service.messaging;

import com.moviebooking.reservation_service.config.RabbitMQConfig;
import com.moviebooking.reservation_service.event.ReservationSagaEvent;
import com.moviebooking.reservation_service.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationSagaListener {

    private final ReservationRepository reservationRepository;

    @RabbitListener(queues = RabbitMQConfig.RESERVATION_UPDATE_QUEUE)
    public void onSagaEvent(ReservationSagaEvent event, Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info("Reservation {} received: {}", event.getReservationId(), routingKey);

        switch (routingKey) {
            case "ticket.reserved" -> reservationRepository.updatePriceIfPending(
                    event.getReservationId(), event.getTotalPrice());

            case "ticket.reservation.failed", "payment.failed" -> {
                int updated = reservationRepository.cancelIfStillPending(event.getReservationId());
                if (updated == 0) log.info("Reservation {} already resolved, ignoring", event.getReservationId());
            }

            case "payment.succeeded" -> {
                int updated = reservationRepository.confirmIfStillPending(event.getReservationId());
                if (updated == 0) log.info("Reservation {} already resolved, ignoring", event.getReservationId());
            }

            default -> log.warn("Unhandled routing key: {}", routingKey);
        }
    }
}