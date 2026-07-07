package com.moviebooking.payment_service.client;

import com.moviebooking.payment_service.client.dto.ShowDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "show-service", contextId = "showServiceClient", path = "/api/shows")
public interface ShowServiceClient {

    @GetMapping("/{id}")
    ShowDto getShowById(@PathVariable("id") UUID id);
}
