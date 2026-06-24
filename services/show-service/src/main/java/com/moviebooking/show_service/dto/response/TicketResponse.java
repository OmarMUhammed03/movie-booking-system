package com.moviebooking.show_service.dto.response;

import com.moviebooking.show_service.model.TicketStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketResponse(
        UUID id, UUID showId, String seatNumber, BigDecimal price, TicketStatus status,
        LocalDateTime createdAt) {

}
