package com.moviebooking.show_service.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record HallResponse(
        UUID id, String name, Integer totalSeats, Integer rowCount, Integer seatsPerRow,
        String screenType, LocalDateTime createdAt) {

}
