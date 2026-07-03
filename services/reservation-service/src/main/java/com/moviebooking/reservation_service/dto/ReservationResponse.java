package com.moviebooking.reservation_service.dto;

import com.moviebooking.reservation_service.model.ReservationStatus;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ReservationResponse {
    private UUID id;
    private UUID userId;
    private UUID showId;
    private BigDecimal totalPrice;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    private List<UUID> ticketIds;
}