package com.moviebooking.reservation_service.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class ReservationRequest {
    private UUID userId;
    private UUID showId;
    private List<UUID> ticketIds;
}
