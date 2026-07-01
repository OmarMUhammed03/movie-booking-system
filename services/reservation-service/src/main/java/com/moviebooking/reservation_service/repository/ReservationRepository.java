package com.moviebooking.reservation_service.repository;

import com.moviebooking.reservation_service.model.Reservation;
import com.moviebooking.reservation_service.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    List<Reservation> findByUserId(UUID userId);

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByShowId(UUID showId);

    List<Reservation> findByUserIdAndStatus(UUID userId, ReservationStatus status);

    long countByUserId(UUID userId);

    boolean existsByUserIdAndStatus(UUID userId, ReservationStatus status);
}