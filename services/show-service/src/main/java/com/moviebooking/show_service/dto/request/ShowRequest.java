package com.moviebooking.show_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ShowRequest(
        @NotNull(message = "Movie id is required") UUID movieId,

        @NotNull(message = "Hall id is required") UUID hallId,

        @NotNull(message = "Start time is required") LocalDateTime startTime,
        LocalDateTime endTime,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        BigDecimal price
) {
}
