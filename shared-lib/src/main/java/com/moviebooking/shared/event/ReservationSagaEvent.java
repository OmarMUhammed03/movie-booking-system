package com.moviebooking.shared.event;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationSagaEvent {
    private UUID reservationId;
    private UUID userId;
    private UUID showId;
    private List<UUID> ticketIds;
    private BigDecimal totalPrice;
    private String reason; // populated only on failure events
}