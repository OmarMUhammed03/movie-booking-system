package com.moviebooking.reservation_service.repository;

import com.moviebooking.reservation_service.model.Reservation;
import com.moviebooking.reservation_service.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal; // 1. Imported BigDecimal
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

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Reservation r SET r.status = 'CONFIRMED' WHERE r.id = :id AND r.status = 'PENDING'")
    int confirmIfStillPending(@Param("id") UUID id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Reservation r SET r.status = 'CANCELLED' WHERE r.id = :id AND r.status = 'PENDING'")
    int cancelIfStillPending(@Param("id") UUID id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Reservation r SET r.totalPrice = :price WHERE r.id = :id AND r.status = 'PENDING'")
    int updatePriceIfPending(@Param("id") UUID id, @Param("price") BigDecimal price);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Reservation r SET r.status = 'CONFIRMED', r.totalPrice = :price WHERE r.id = :id AND r.status = 'PENDING'")
    int confirmAndUpdatePriceIfStillPending(@Param("id") UUID id, @Param("price") BigDecimal price);
}