package com.moviebooking.show_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ShowResponse(
        UUID id, UUID movieId, UUID hallId, LocalDateTime startTime, LocalDateTime endTime,
        BigDecimal price, LocalDateTime createdAt) {

}
