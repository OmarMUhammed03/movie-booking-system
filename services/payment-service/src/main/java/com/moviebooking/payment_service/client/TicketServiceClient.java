package com.moviebooking.payment_service.client;

import com.moviebooking.payment_service.client.dto.TicketDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "show-service", contextId = "ticketServiceClient", path = "/api/shows/tickets")
public interface TicketServiceClient {

    @GetMapping("/{id}")
    TicketDto getTicketById(@PathVariable("id") UUID id);
}
