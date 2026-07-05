package com.moviebooking.reservation_service.controller;

import com.moviebooking.reservation_service.dto.ReservationRequest;
import com.moviebooking.reservation_service.dto.ReservationResponse;
import com.moviebooking.reservation_service.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> create(@RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(reservationService.createReservation(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(reservationService.getReservation(id));
    }
}