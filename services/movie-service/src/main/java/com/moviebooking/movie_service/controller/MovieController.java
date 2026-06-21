package com.moviebooking.movie_service.controller;

import com.moviebooking.movie_service.dto.request.MovieRequest;
import com.moviebooking.movie_service.dto.response.MovieResponse;
import com.moviebooking.movie_service.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    // TODO: @PreAuthorize("hasRole('ADMIN')") Security layer
    @PostMapping
    public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieRequest request) {
        MovieResponse response = movieService.createMovie(request);
        URI location = URI.create("/api/movies/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable UUID id) {
        return ResponseEntity.ok(movieService.getMovieById(id));
    }

    @GetMapping
    public ResponseEntity<List<MovieResponse>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    // TODO: @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MovieResponse> updateMovie(
            @PathVariable UUID id,
            @Valid @RequestBody MovieRequest request) {
        return ResponseEntity.ok(movieService.updateMovie(id, request));
    }

    // TODO: @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable UUID id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}