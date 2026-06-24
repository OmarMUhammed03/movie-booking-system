package com.moviebooking.show_service.repository;

import com.moviebooking.show_service.model.Hall;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HallRepository extends JpaRepository<Hall, UUID> {
    Optional<Hall> findHallByNameIgnoreCase(String name);
}
