package com.moviebooking.show_service.repository;

import com.moviebooking.show_service.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findTicketsByShowId(UUID showId);

    @Modifying
    @Query("UPDATE Ticket t SET t.status = 'RESERVED' WHERE t.id IN :ids AND t.showId = :showId AND t.status = 'AVAILABLE'")
    int reserveIfAvailable(@Param("ids") List<UUID> ids, @Param("showId") UUID showId);

    @Modifying
    @Query("UPDATE Ticket t SET t.status = 'BOOKED' WHERE t.id IN :ids AND t.status = 'RESERVED'")
    int bookIfReserved(@Param("ids") List<UUID> ids);

    @Modifying
    @Query("UPDATE Ticket t SET t.status = 'AVAILABLE' WHERE t.id IN :ids AND t.status = 'RESERVED'")
    int releaseIfReserved(@Param("ids") List<UUID> ids);

    @Query("SELECT COALESCE(SUM(t.price), 0) FROM Ticket t WHERE t.id IN :ids")
    BigDecimal sumPriceByIds(@Param("ids") List<UUID> ids);
}
