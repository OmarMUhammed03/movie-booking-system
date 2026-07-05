package com.moviebooking.reservation_service.mapper;

import com.moviebooking.reservation_service.dto.ReservationResponse;
import com.moviebooking.reservation_service.model.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {
    public ReservationResponse toResponse(Reservation r) {
        return ReservationResponse.builder()
                .id(r.getId())
                .userId(r.getUserId())
                .showId(r.getShowId())
                .totalPrice(r.getTotalPrice())
                .status(r.getStatus())
                .createdAt(r.getCreatedAt())
                .ticketIds(r.getReservationTickets() == null ? null :
                        r.getReservationTickets().stream().map(rt -> rt.getTicketId()).toList())
                .build();
    }
}