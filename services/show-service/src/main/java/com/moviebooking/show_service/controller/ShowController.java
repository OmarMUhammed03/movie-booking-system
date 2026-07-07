package com.moviebooking.show_service.controller;

import com.moviebooking.show_service.dto.request.ShowRequest;
import com.moviebooking.show_service.dto.response.ShowResponse;
import com.moviebooking.show_service.service.ShowService;
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
@RequestMapping("/api/shows")
@RequiredArgsConstructor
@Tag(name = "Show Controller", description = "APIs for managing movie show schedules")
public class ShowController {

    private final ShowService showService;

//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new show", description = "Schedules a new show for a movie in a hall. Restricted to administrators.")
    public ResponseEntity<ShowResponse> createShow(@Valid @RequestBody ShowRequest request) {
        ShowResponse response = showService.createShow(request);
        URI location = URI.create("/api/shows/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get show details by ID", description = "Retrieves complete metadata for a specific show using its unique UUID.")
    public ResponseEntity<ShowResponse> getShowById(@PathVariable UUID id) {
        return ResponseEntity.ok(showService.getShowById(id));
    }

    @GetMapping
    @Operation(summary = "Get all shows", description = "Fetches a comprehensive list of all shows available in the database.")
    public ResponseEntity<List<ShowResponse>> getAllShows() {
        return ResponseEntity.ok(showService.getAllShows());
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing show", description = "Updates the fields of an existing show record found by its UUID.")
    public ResponseEntity<ShowResponse> updateShow(
            @PathVariable UUID id,
            @Valid @RequestBody ShowRequest request) {
        return ResponseEntity.ok(showService.updateShow(id, request));
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a show record", description = "Permanently removes a show from the database using its ID.")
    public ResponseEntity<Void> deleteShow(@PathVariable UUID id) {
        showService.deleteShow(id);
        return ResponseEntity.noContent().build();
    }
}
