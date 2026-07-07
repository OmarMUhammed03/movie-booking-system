package com.moviebooking.show_service.messaging;

import com.moviebooking.show_service.config.RabbitMQConfig;
import com.moviebooking.show_service.service.TicketService;
import com.moviebooking.shared.event.ReservationSagaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Show-service's half of the booking saga choreography.
 * <p>
 * reservation.created  -> reserve the tickets, reply ticket.reserved (with total price)
 *                         or ticket.reservation.failed (with reason)
 * payment.succeeded    -> RESERVED tickets become BOOKED
 * payment.failed       -> RESERVED tickets are released back to AVAILABLE (compensation)
 * <p>
 * Deliberately NOT @Transactional: each TicketService method owns its transaction, so a
 * failed reservation rolls back fully and we can still publish the failure event afterwards.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TicketSagaListener {

    private final TicketService ticketService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.TICKET_UPDATE_QUEUE)
    public void onSagaEvent(ReservationSagaEvent event, Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info("Reservation {} received event: {}", event.getReservationId(), routingKey);

        switch (routingKey) {
            case RabbitMQConfig.ROUTING_KEY_RESERVATION_CREATED -> handleReservationCreated(event);
            case RabbitMQConfig.ROUTING_KEY_PAYMENT_SUCCEEDED -> ticketService.bookTickets(event.getTicketIds());
            case RabbitMQConfig.ROUTING_KEY_PAYMENT_FAILED -> ticketService.releaseTickets(event.getTicketIds());
            default -> log.warn("Unhandled routing key: {}", routingKey);
        }
    }

    private void handleReservationCreated(ReservationSagaEvent event) {
        try {
            BigDecimal totalPrice = ticketService.reserveTickets(event.getTicketIds(), event.getShowId());
            event.setTotalPrice(totalPrice);
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TICKET_EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY_TICKET_RESERVED,
                    event
            );
            log.info("Reservation {}: tickets reserved, total {}", event.getReservationId(), totalPrice);
        } catch (Exception e) {
            event.setReason(e.getMessage());
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.TICKET_EXCHANGE,
                    RabbitMQConfig.ROUTING_KEY_TICKET_FAILED,
                    event
            );
            log.warn("Reservation {}: ticket reservation failed ({})", event.getReservationId(), e.getMessage());
        }
    }
}
