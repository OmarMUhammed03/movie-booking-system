package com.moviebooking.reservation_service.service;

import com.moviebooking.reservation_service.config.RabbitMQConfig;
import com.moviebooking.reservation_service.dto.ReservationRequest;
import com.moviebooking.reservation_service.dto.ReservationResponse;
import com.moviebooking.reservation_service.exception.ReservationNotFoundException;
import com.moviebooking.reservation_service.mapper.ReservationMapper;
import com.moviebooking.reservation_service.model.Reservation;
import com.moviebooking.reservation_service.model.ReservationTicket;
import com.moviebooking.reservation_service.repository.ReservationRepository;
import com.moviebooking.reservation_service.repository.ReservationTicketRepository;
import com.moviebooking.shared.event.ReservationSagaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTicketRepository reservationTicketRepository;
    private final ReservationMapper reservationMapper;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {

        Reservation reservation = Reservation.builder()
                .userId(request.getUserId())
                .showId(request.getShowId())
                .totalPrice(BigDecimal.ZERO) // filled in once ticket.reserved arrives
                .build();

        Reservation saved = reservationRepository.save(reservation);

        request.getTicketIds().forEach(ticketId ->
                reservationTicketRepository.save(
                        ReservationTicket.builder()
                                .reservation(saved)
                                .ticketId(ticketId)
                                .build()
                )
        );

        ReservationSagaEvent event = ReservationSagaEvent.builder()
                .reservationId(saved.getId())
                .userId(saved.getUserId())
                .showId(saved.getShowId())
                .ticketIds(request.getTicketIds())
                .build();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.RESERVATION_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_RESERVATION_CREATED,
                event
        );

        return reservationMapper.toResponse(saved);
    }

    public ReservationResponse getReservation(UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
        return reservationMapper.toResponse(reservation);
    }
    public List<ReservationResponse> getReservationsByUserId(UUID userId) {
        return reservationRepository.findByUserId(userId)
                .stream()
                .map(reservationMapper::toResponse)
                .collect(Collectors.toList());
    }
}