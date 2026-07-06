package com.moviebooking.ai_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ReservationDto(
        UUID id, UUID userId, UUID showId, BigDecimal totalPrice, String status,
        LocalDateTime createdAt, List<UUID> ticketIds) {
}
