package com.moviebooking.ai_service.client;

import com.moviebooking.ai_service.dto.ReservationDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "reservation-service", path = "/api/reservations")
public interface ReservationClient {

    @GetMapping("/user/{userId}")
    List<ReservationDto> getByUserId(@PathVariable("userId") UUID userId);
}
