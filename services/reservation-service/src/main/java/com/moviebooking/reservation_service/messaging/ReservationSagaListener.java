package com.moviebooking.reservation_service.messaging;

import com.moviebooking.reservation_service.config.RabbitMQConfig;
import com.moviebooking.reservation_service.event.ReservationSagaEvent;
import com.moviebooking.reservation_service.model.Reservation;
import com.moviebooking.reservation_service.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationSagaListener {

    private final ReservationRepository reservationRepository;

    @RabbitListener(queues = RabbitMQConfig.RESERVATION_UPDATE_QUEUE)
    @Transactional
    public void onSagaEvent(ReservationSagaEvent event, Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info("Reservation {} received event: {}", event.getReservationId(), routingKey);

        Reservation reservation = reservationRepository.findById(event.getReservationId()).orElse(null);
        if (reservation == null) {
            log.warn("Reservation {} not found in database. Ignoring event.", event.getReservationId());
            return;
        }

        if (!Objects.equals(reservation.getUserId(), event.getUserId())) {
            log.warn("User ID mismatch for reservation {}. Ignoring event.", event.getReservationId());
            return;
        }

        switch (routingKey) {
            case RabbitMQConfig.ROUTING_KEY_TICKET_RESERVED -> reservationRepository.updatePriceIfPending(
                    event.getReservationId(), event.getTotalPrice());

            case RabbitMQConfig.ROUTING_KEY_TICKET_FAILED, RabbitMQConfig.ROUTING_KEY_PAYMENT_FAILED -> {
                int updated = reservationRepository.cancelIfStillPending(event.getReservationId());
                if (updated == 0) log.info("Reservation {} already resolved, ignoring", event.getReservationId());
            }

            case RabbitMQConfig.ROUTING_KEY_PAYMENT_SUCCEEDED -> {
                int updated = reservationRepository.confirmIfStillPending(event.getReservationId());
                if (updated == 0) log.info("Reservation {} already resolved, ignoring", event.getReservationId());
            }

            default -> log.warn("Unhandled routing key: {}", routingKey);
        }
    }
}