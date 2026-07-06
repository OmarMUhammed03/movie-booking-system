package com.moviebooking.show_service.service.impl;

import com.moviebooking.show_service.dto.request.TicketRequest;
import com.moviebooking.show_service.dto.response.TicketResponse;
import com.moviebooking.show_service.exception.TicketNotFoundException;
import com.moviebooking.show_service.mapper.TicketMapper;
import com.moviebooking.show_service.model.Ticket;
import com.moviebooking.show_service.repository.TicketRepository;
import com.moviebooking.show_service.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    @Override
    @Transactional
    public TicketResponse createTicket(TicketRequest request) {
        Ticket ticket = ticketMapper.toEntity(request);
        Ticket saved = ticketRepository.save(ticket);
        return ticketMapper.toResponse(saved);
    }

    @Override
    public TicketResponse getTicketById(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        return ticketMapper.toResponse(ticket);
    }

    @Override
    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(ticketMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TicketResponse updateTicket(UUID id, TicketRequest request) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));

        ticketMapper.updateEntityFromRequest(request, ticket);
        Ticket updated = ticketRepository.save(ticket);
        return ticketMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteTicket(UUID id) {
        if (!ticketRepository.existsById(id)) {
            throw new TicketNotFoundException(id);
        }
        ticketRepository.deleteById(id);
    }
}
