package com.moviebooking.payment_service.client.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketDto(
        UUID id,
        UUID showId,
        String seatNumber,
        BigDecimal price,
        String status,
        LocalDateTime createdAt
) {
}
