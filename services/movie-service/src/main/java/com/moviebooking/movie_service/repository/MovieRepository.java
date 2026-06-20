package com.moviebooking.movie_service.repository;

import com.moviebooking.movie_service.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MovieRepository extends JpaRepository<Movie, UUID> {
    Optional<Movie> findMovieByTitleIgnoreCase(String title);
}
