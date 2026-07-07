package com.moviebooking.reservation_service.controller;

import com.moviebooking.reservation_service.dto.ReservationRequest;
import com.moviebooking.reservation_service.dto.ReservationResponse;
import com.moviebooking.reservation_service.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationResponse> create(@RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(reservationService.createReservation(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationResponse>> getByUserId(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(reservationService.getReservationsByUserId(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> get( @PathVariable UUID id) {
        return ResponseEntity.ok(reservationService.getReservation(id));
    }

}