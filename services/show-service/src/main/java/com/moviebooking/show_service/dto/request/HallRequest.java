package com.moviebooking.show_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record HallRequest(
        @NotBlank(message = "Hall name is required") String name,

        @NotNull(message = "Total seats is required")
        @Positive(message = "Total seats must be positive")
        Integer totalSeats,

        @Positive(message = "Row count must be positive")
        Integer rowCount,

        @Positive(message = "Seats per row must be positive")
        Integer seatsPerRow,

        String screenType
) {
}
