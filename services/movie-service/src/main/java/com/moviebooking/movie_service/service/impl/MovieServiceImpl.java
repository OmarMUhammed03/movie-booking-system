package com.moviebooking.movie_service.service.impl;

import com.moviebooking.movie_service.dto.request.MovieRequest;
import com.moviebooking.movie_service.dto.response.MovieResponse;
import com.moviebooking.movie_service.exception.MovieNotFoundException;
import com.moviebooking.movie_service.mapper.MovieMapper;
import com.moviebooking.movie_service.model.Movie;
import com.moviebooking.movie_service.repository.MovieRepository;
import com.moviebooking.movie_service.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;

    @Override
    @Transactional
    public MovieResponse createMovie(MovieRequest request) {
        Movie movie = movieMapper.toEntity(request);
        Movie saved = movieRepository.save(movie);
        return movieMapper.toResponse(saved);
    }

    @Override
    public MovieResponse getMovieById(UUID id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));
        return movieMapper.toResponse(movie);
    }

    @Override
    public Page<MovieResponse> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable)
                .map(movieMapper::toResponse);
    }

//    @Override
//    public List<MovieResponse> getAllMovies() {
//        return movieRepository.findAll()
//                .stream()
//                .map(movieMapper::toResponse)
//                .toList();
//    }

    @Override
    @Transactional
    public MovieResponse updateMovie(UUID id, MovieRequest request) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new MovieNotFoundException(id));

        movieMapper.updateEntityFromRequest(request, movie);
        Movie updated = movieRepository.save(movie);
        return movieMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteMovie(UUID id) {
        if (!movieRepository.existsById(id)) {
            throw new MovieNotFoundException(id);
        }
        movieRepository.deleteById(id);
    }
}