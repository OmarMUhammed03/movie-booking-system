package com.moviebooking.payment_service.client.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record MovieDto(
        UUID id,
        String title,
        String description,
        String genre,
        Integer durationMinutes,
        String posterUrl,
        LocalDate releaseDate,
        LocalDateTime createdAt
) {
}
