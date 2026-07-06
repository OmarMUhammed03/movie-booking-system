package com.moviebooking.show_service.service.impl;

import com.moviebooking.show_service.dto.request.TicketRequest;
import com.moviebooking.show_service.dto.response.TicketResponse;
import com.moviebooking.show_service.exception.TicketNotFoundException;
import com.moviebooking.show_service.mapper.TicketMapper;
import com.moviebooking.show_service.model.Ticket;
import com.moviebooking.show_service.repository.TicketRepository;
import com.moviebooking.show_service.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
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

    /**
     * All-or-nothing: if any requested ticket is not AVAILABLE (or belongs to another
     * show), the transaction rolls back and no seat is touched.
     */
    @Override
    @Transactional
    public BigDecimal reserveTickets(List<UUID> ticketIds, UUID showId) {
        if (ticketIds == null || ticketIds.isEmpty()) {
            throw new IllegalArgumentException("Reservation contains no tickets");
        }
        int reserved = ticketRepository.reserveIfAvailable(ticketIds, showId);
        if (reserved != ticketIds.size()) {
            throw new IllegalStateException(
                    "Only " + reserved + " of " + ticketIds.size() + " tickets are available for this show");
        }
        return ticketRepository.sumPriceByIds(ticketIds);
    }

    @Override
    @Transactional
    public void bookTickets(List<UUID> ticketIds) {
        if (ticketIds == null || ticketIds.isEmpty()) {
            return;
        }
        int booked = ticketRepository.bookIfReserved(ticketIds);
        if (booked != ticketIds.size()) {
            log.warn("Booked {} of {} tickets; the rest were not in RESERVED state (duplicate or late event?)",
                    booked, ticketIds.size());
        }
    }

    @Override
    @Transactional
    public void releaseTickets(List<UUID> ticketIds) {
        if (ticketIds == null || ticketIds.isEmpty()) {
            return;
        }
        int released = ticketRepository.releaseIfReserved(ticketIds);
        if (released != ticketIds.size()) {
            log.warn("Released {} of {} tickets; the rest were not in RESERVED state (duplicate or late event?)",
                    released, ticketIds.size());
        }
    }
}
