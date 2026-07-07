package com.moviebooking.show_service.service;

import com.moviebooking.show_service.dto.request.TicketRequest;
import com.moviebooking.show_service.dto.response.TicketResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TicketService {
    TicketResponse createTicket(TicketRequest request);
    TicketResponse getTicketById(UUID id);
    List<TicketResponse> getAllTickets();
    TicketResponse updateTicket(UUID id, TicketRequest request);
    void deleteTicket(UUID id);

    // saga operations (driven by RabbitMQ events, not REST)
    BigDecimal reserveTickets(List<UUID> ticketIds, UUID showId);
    void bookTickets(List<UUID> ticketIds);
    void releaseTickets(List<UUID> ticketIds);
}
