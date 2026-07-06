package com.moviebooking.ai_service.client;

import com.moviebooking.ai_service.dto.ShowDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "show-service", path = "/api/shows")
public interface ShowClient {

    @GetMapping
    List<ShowDto> getAllShows();

    @GetMapping("/{id}")
    ShowDto getShowById(@PathVariable("id") UUID id);
}
