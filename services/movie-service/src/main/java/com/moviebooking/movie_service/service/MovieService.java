package com.moviebooking.movie_service.service;

import com.moviebooking.movie_service.dto.request.MovieRequest;
import com.moviebooking.movie_service.dto.response.MovieResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MovieService {
    MovieResponse createMovie(MovieRequest request);

    MovieResponse getMovieById(UUID id);

    Page<MovieResponse> getAllMovies(Pageable pageable);

    MovieResponse updateMovie(UUID id, MovieRequest request);

    void deleteMovie(UUID id);
}