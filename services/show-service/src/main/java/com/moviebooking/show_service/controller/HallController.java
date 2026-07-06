package com.moviebooking.show_service.controller;

import com.moviebooking.show_service.dto.request.HallRequest;
import com.moviebooking.show_service.dto.response.HallResponse;
import com.moviebooking.show_service.service.HallService;
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
@RequestMapping("/api/halls")
@RequiredArgsConstructor
@Tag(name = "Hall Controller", description = "APIs for managing cinema halls")
public class HallController {

    private final HallService hallService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new hall", description = "Adds a new cinema hall to the system. Restricted to administrators.")
    public ResponseEntity<HallResponse> createHall(@Valid @RequestBody HallRequest request) {
        HallResponse response = hallService.createHall(request);
        URI location = URI.create("/api/halls/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get hall details by ID", description = "Retrieves complete metadata for a specific hall using its unique UUID.")
    public ResponseEntity<HallResponse> getHallById(@PathVariable UUID id) {
        return ResponseEntity.ok(hallService.getHallById(id));
    }

    @GetMapping
    @Operation(summary = "Get all halls", description = "Fetches a comprehensive list of all halls available in the database.")
    public ResponseEntity<List<HallResponse>> getAllHalls() {
        return ResponseEntity.ok(hallService.getAllHalls());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing hall", description = "Updates the fields of an existing hall record found by its UUID.")
    public ResponseEntity<HallResponse> updateHall(
            @PathVariable UUID id,
            @Valid @RequestBody HallRequest request) {
        return ResponseEntity.ok(hallService.updateHall(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a hall record", description = "Permanently removes a hall from the database using its ID.")
    public ResponseEntity<Void> deleteHall(@PathVariable UUID id) {
        hallService.deleteHall(id);
        return ResponseEntity.noContent().build();
    }
}
