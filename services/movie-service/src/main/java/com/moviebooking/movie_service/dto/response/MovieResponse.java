package com.moviebooking.movie_service.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record MovieResponse(
        UUID id, String title, String description, String genre, Integer durationMinutes, String posterUrl,
        LocalDate releaseDate, LocalDateTime createdAt) {

}