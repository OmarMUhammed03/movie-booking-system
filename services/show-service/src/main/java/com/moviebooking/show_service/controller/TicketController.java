package com.moviebooking.show_service.controller;

import com.moviebooking.show_service.dto.request.TicketRequest;
import com.moviebooking.show_service.dto.response.TicketResponse;
import com.moviebooking.show_service.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shows/tickets")
@RequiredArgsConstructor
@Tag(name = "Ticket Controller", description = "APIs for managing show tickets")
public class TicketController {

    private final TicketService ticketService;

//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new ticket", description = "Generates a new ticket for a seat in a show. Restricted to administrators.")
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody TicketRequest request) {
        TicketResponse response = ticketService.createTicket(request);
        URI location = URI.create("/api/shows/tickets/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ticket details by ID", description = "Retrieves complete metadata for a specific ticket using its unique UUID.")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable UUID id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @GetMapping
    @Operation(summary = "Get all tickets", description = "Fetches tickets available in the database, optionally filtered by show.")
    public ResponseEntity<List<TicketResponse>> getAllTickets(
            @RequestParam(required = false) UUID showId) {
        if (showId != null) {
            return ResponseEntity.ok(ticketService.getTicketsByShowId(showId));
        }
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing ticket", description = "Updates the fields of an existing ticket record found by its UUID.")
    public ResponseEntity<TicketResponse> updateTicket(
            @PathVariable UUID id,
            @Valid @RequestBody TicketRequest request) {
        return ResponseEntity.ok(ticketService.updateTicket(id, request));
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a ticket record", description = "Permanently removes a ticket from the database using its ID.")
    public ResponseEntity<Void> deleteTicket(@PathVariable UUID id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
