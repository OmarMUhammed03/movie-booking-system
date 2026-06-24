package com.moviebooking.show_service.service;

import com.moviebooking.show_service.dto.request.TicketRequest;
import com.moviebooking.show_service.dto.response.TicketResponse;

import java.util.List;
import java.util.UUID;

public interface TicketService {
    TicketResponse createTicket(TicketRequest request);
    TicketResponse getTicketById(UUID id);
    List<TicketResponse> getAllTickets();
    TicketResponse updateTicket(UUID id, TicketRequest request);
    void deleteTicket(UUID id);
}
