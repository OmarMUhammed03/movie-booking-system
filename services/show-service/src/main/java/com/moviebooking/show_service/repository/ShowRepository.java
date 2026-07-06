package com.moviebooking.show_service.repository;

import com.moviebooking.show_service.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShowRepository extends JpaRepository<Show, UUID> {
    List<Show> findShowsByMovieId(UUID movieId);
    List<Show> findShowsByHallId(UUID hallId);
}
