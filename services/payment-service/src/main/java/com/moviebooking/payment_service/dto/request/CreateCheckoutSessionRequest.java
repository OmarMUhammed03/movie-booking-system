package com.moviebooking.payment_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateCheckoutSessionRequest(
        @NotNull(message = "reservationId is required")
        UUID reservationId,

        @NotNull(message = "userId is required")
        UUID userId,

        @NotNull(message = "showId is required")
        UUID showId,

        @NotEmpty(message = "ticketIds must not be empty")
        List<UUID> ticketIds,

        @NotNull(message = "totalPrice is required")
        @DecimalMin(value = "0.50", message = "totalPrice must be at least 0.50")
        BigDecimal totalPrice
) {
}
