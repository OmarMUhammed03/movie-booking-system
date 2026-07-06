package com.moviebooking.reservation_service.repository;

import com.moviebooking.reservation_service.model.ReservationTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationTicketRepository extends JpaRepository<ReservationTicket, UUID> {

    List<ReservationTicket> findByReservationId(UUID reservationId);

    List<ReservationTicket> findByTicketId(UUID ticketId);

    void deleteByReservationId(UUID reservationId);

    boolean existsByTicketId(UUID ticketId);

    long countByReservationId(UUID reservationId);
}