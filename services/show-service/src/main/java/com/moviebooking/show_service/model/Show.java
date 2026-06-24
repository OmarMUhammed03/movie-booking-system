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
public class Show {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private UUID movieId;
    @Column(nullable = false)
    private UUID hallId;
    @Column(nullable = false)
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Column(nullable = false)
    private BigDecimal price;
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
