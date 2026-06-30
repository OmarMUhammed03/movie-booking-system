package com.moviebooking.movie_service.controller;

import com.moviebooking.movie_service.dto.request.MovieRequest;
import com.moviebooking.movie_service.dto.response.MovieResponse;
import com.moviebooking.movie_service.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
@Tag(name = "Movie Controller", description = "APIs for managing movie catalog records")
public class MovieController {

    private final MovieService movieService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new movie", description = "Adds a new movie to the catalog system. Restricted to administrators.")
    public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieRequest request) {
        MovieResponse response = movieService.createMovie(request);
        URI location = URI.create("/api/movies/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get movie details by ID", description = "Retrieves complete metadata for a specific movie using its unique UUID.")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable UUID id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @GetMapping
    @Operation(summary = "Get paginated movies", description = "Fetches a paginated and sorted list of movies available in the database.")    public ResponseEntity<Page<MovieResponse>> getAllMovies(
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(movieService.getAllMovies(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing movie", description = "Updates the fields of an existing movie record found by its UUID.")
    public ResponseEntity<MovieResponse> updateMovie(
            @PathVariable UUID id,
            @Valid @RequestBody MovieRequest request) {
        return ResponseEntity.ok(movieService.updateMovie(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a movie record", description = "Permanently removes a movie from the database configuration using its ID.")
    public ResponseEntity<Void> deleteMovie(@PathVariable UUID id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}