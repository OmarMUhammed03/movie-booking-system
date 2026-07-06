package com.moviebooking.ai_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ShowDto(
        UUID id, UUID movieId, UUID hallId, LocalDateTime startTime, LocalDateTime endTime,
        BigDecimal price, LocalDateTime createdAt) {
}
