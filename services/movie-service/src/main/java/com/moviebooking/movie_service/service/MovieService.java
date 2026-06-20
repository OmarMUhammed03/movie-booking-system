package com.moviebooking.movie_service.service;

import com.moviebooking.movie_service.dto.request.MovieRequest;
import com.moviebooking.movie_service.dto.response.MovieResponse;

import java.util.List;
import java.util.UUID;

public interface MovieService {
    MovieResponse createMovie(MovieRequest request);
    MovieResponse getMovieById(UUID id);
    List<MovieResponse> getAllMovies();
    MovieResponse updateMovie(UUID id, MovieRequest request);
    void deleteMovie(UUID id);
}