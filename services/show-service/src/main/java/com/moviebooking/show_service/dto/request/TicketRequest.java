package com.moviebooking.show_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record TicketRequest(
        @NotNull(message = "Show id is required") UUID showId,

        @NotBlank(message = "Seat number is required") String seatNumber,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        BigDecimal price
) {
}
