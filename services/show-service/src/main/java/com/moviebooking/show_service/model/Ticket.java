package com.moviebooking.show_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private UUID showId;
    @Column(nullable = false)
    private String seatNumber;
    @Column(nullable = false)
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = TicketStatus.AVAILABLE;
        }
    }
}
