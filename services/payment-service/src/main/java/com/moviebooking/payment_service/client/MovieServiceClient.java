package com.moviebooking.payment_service.client;

import com.moviebooking.payment_service.client.dto.MovieDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "movie-service", path = "/api/movies")
public interface MovieServiceClient {

    @GetMapping("/{id}")
    MovieDto getMovieById(@PathVariable("id") UUID id);
}
