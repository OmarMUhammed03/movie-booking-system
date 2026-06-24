package com.moviebooking.show_service.repository;

import com.moviebooking.show_service.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findTicketsByShowId(UUID showId);
}
