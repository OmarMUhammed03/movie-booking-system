package com.moviebooking.movie_service.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record MovieRequest(
        @NotBlank(message = "Title is required") String title,
        String description,

        @NotNull(message = "Duration is required")
        @Positive(message = "Duration must be positive")
        Integer durationMinutes,
        String posterUrl,

        @NotNull(message = "Release date is required")
        LocalDate releaseDate
) {
}
