package com.moviebooking.ai_service.client;

import com.moviebooking.ai_service.dto.MovieDto;
import com.moviebooking.ai_service.dto.PageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "movie-service", path = "/api/movies")
public interface MovieClient {

    @GetMapping
    PageDto<MovieDto> getMovies(@RequestParam("page") int page, @RequestParam("size") int size);

    @GetMapping("/{id}")
    MovieDto getMovieById(@PathVariable("id") UUID id);
}
