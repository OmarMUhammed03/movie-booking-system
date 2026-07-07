package com.moviebooking.payment_service.service.impl;

import com.moviebooking.payment_service.client.MovieServiceClient;
import com.moviebooking.payment_service.client.ShowServiceClient;
import com.moviebooking.payment_service.client.TicketServiceClient;
import com.moviebooking.payment_service.client.dto.MovieDto;
import com.moviebooking.payment_service.client.dto.ShowDto;
import com.moviebooking.payment_service.client.dto.TicketDto;
import com.moviebooking.payment_service.dto.CheckoutSessionDetails;
import com.moviebooking.payment_service.exception.PaymentProcessingException;
import com.moviebooking.payment_service.service.CheckoutDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckoutDetailsServiceImpl implements CheckoutDetailsService {

    private static final DateTimeFormatter SHOWTIME_FORMAT = DateTimeFormatter.ofPattern("EEE, MMM d yyyy 'at' h:mm a");

    private final ShowServiceClient showServiceClient;
    private final MovieServiceClient movieServiceClient;
    private final TicketServiceClient ticketServiceClient;

    @Override
    public CheckoutSessionDetails resolve(UUID showId, List<UUID> ticketIds) {
        ShowDto show = showServiceClient.getShowById(showId);
        MovieDto movie = movieServiceClient.getMovieById(show.movieId());

        List<TicketDto> tickets = ticketIds.stream()
                .map(ticketServiceClient::getTicketById)
                .toList();

        if (tickets.stream().anyMatch(t -> !showId.equals(t.showId()))) {
            throw new PaymentProcessingException("One or more tickets do not belong to show " + showId);
        }

        String seatNumbers = tickets.stream()
                .map(TicketDto::seatNumber)
                .collect(Collectors.joining(", "));

        String description = buildDescription(movie, show, seatNumbers);

        return new CheckoutSessionDetails(movie.title(), description, movie.posterUrl());
    }

    private String buildDescription(MovieDto movie, ShowDto show, String seatNumbers) {
        StringBuilder description = new StringBuilder();

        if (movie.description() != null && !movie.description().isBlank()) {
            description.append(movie.description().trim());
            description.append("\n\n");
        }

        description.append("Showtime: ").append(show.startTime().format(SHOWTIME_FORMAT));
        description.append("\nSeats: ").append(seatNumbers);

        return description.toString();
    }
}
