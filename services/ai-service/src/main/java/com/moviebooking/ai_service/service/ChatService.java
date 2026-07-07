package com.moviebooking.ai_service.service;

import com.moviebooking.ai_service.client.MovieClient;
import com.moviebooking.ai_service.client.ReservationClient;
import com.moviebooking.ai_service.client.ShowClient;
import com.moviebooking.ai_service.dto.MovieDto;
import com.moviebooking.ai_service.dto.ReservationDto;
import com.moviebooking.ai_service.dto.ShowDto;
import com.moviebooking.ai_service.dto.request.ChatRequest;
import com.moviebooking.ai_service.dto.response.ChatResponse;
import com.moviebooking.ai_service.dto.response.SimilarMovieResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private static final int TOP_K = 6;
    private static final int MAX_HISTORY_SAMPLE = 15;
    private static final int MAX_UPCOMING_SHOWS = 8;
    private static final DateTimeFormatter SHOW_TIME = DateTimeFormatter.ofPattern("EEE dd MMM yyyy, HH:mm");

    private static final String PERSONA = """
            You are CineBot, the friendly AI assistant of a movie booking website.
            Rules:
            - Recommend ONLY movies that appear in the catalogue context below. Never invent movies, showtimes or prices.
            - NEVER recommend a movie listed as already watched by the user. You may mention it only as a taste reference ("since you enjoyed ...").
            - You are an assistant, not an agent: you CANNOT book tickets, reserve seats, or process payments, and you must never offer to do so.
              If the user wants to book, tell them to open the movie's page on the website and pick their seats there.
            - If the catalogue has no good match for the request, say so honestly and suggest the closest option.
            - Keep replies short and conversational (2-4 sentences), unless the user asks for more detail.
            - When the user's booking habits are provided, prefer suggesting showtimes near their usual time and mention why.
            - Prices are in EGP. Mention the price and start time when you suggest a specific show.
            """;

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final MovieClient movieClient;
    private final ReservationClient reservationClient;
    private final ShowClient showClient;
    private final MovieSyncService movieSyncService;

    public ChatResponse chat(ChatRequest request) {
        movieSyncService.ensureSynced();

        String conversationId = request.conversationId() != null && !request.conversationId().isBlank()
                ? request.conversationId()
                : UUID.randomUUID().toString();

        StringBuilder context = new StringBuilder();
        String searchQuery = request.message();

        // Reservation history first: it powers both the habit hints and the watched-movie filter
        UserProfile profile = request.userId() != null ? buildUserProfile(request.userId()) : UserProfile.EMPTY;

        // "More like this": ground the search in the movie the user is viewing
        MovieDto currentMovie = null;
        if (request.movieId() != null) {
            try {
                currentMovie = movieClient.getMovieById(request.movieId());
                context.append("The user is currently viewing this movie:\n")
                        .append(describe(currentMovie)).append("\n\n");
                searchQuery = currentMovie.title()
                        + (currentMovie.description() != null ? ". " + currentMovie.description() : "")
                        + ". " + request.message();
            } catch (Exception e) {
                log.warn("Could not fetch current movie {}: {}", request.movieId(), e.getMessage());
            }
        }

        // Semantic retrieval over the embedded catalogue (over-fetch to survive the filters below)
        List<Document> retrieved = vectorStore.similaritySearch(
                SearchRequest.builder().query(searchQuery).topK(TOP_K + profile.watchedMovieIds().size()).build());
        UUID currentId = currentMovie != null ? currentMovie.id() : null;

        List<Document> watchedHits = retrieved.stream()
                .filter(doc -> profile.watchedMovieIds().contains(doc.getId()))
                .toList();
        List<Document> candidates = retrieved.stream()
                .filter(doc -> currentId == null || !doc.getId().equals(currentId.toString()))
                .filter(doc -> !profile.watchedMovieIds().contains(doc.getId()))
                .limit(TOP_K - 1)
                .toList();

        if (candidates.isEmpty()) {
            context.append("Catalogue context: no matching movies were found in the catalogue.\n\n");
        } else {
            context.append("Movies from our catalogue relevant to this request:\n");
            for (Document doc : candidates) {
                context.append("- ").append(doc.getText().replace("\n", " | ")).append("\n");
            }
            context.append("\n");
        }

        if (!watchedHits.isEmpty()) {
            context.append("The user has ALREADY WATCHED these (never recommend them, taste reference only): ")
                    .append(watchedHits.stream()
                            .map(doc -> String.valueOf(doc.getMetadata().getOrDefault("title", "Unknown")))
                            .collect(Collectors.joining(", ")))
                    .append("\n\n");
        }

        // Personalization: usual booking time from reservation history
        if (profile.habits() != null) {
            context.append(profile.habits()).append("\n\n");
        }

        // Ground timing suggestions in real upcoming showtimes
        String upcoming = buildUpcomingShows(candidates, currentMovie);
        if (upcoming != null) {
            context.append(upcoming).append("\n\n");
        }

        String reply = chatClient.prompt()
                .system(PERSONA + "\n--- CONTEXT ---\n" + context)
                .user(request.message())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();

        return new ChatResponse(reply, conversationId);
    }

    public List<SimilarMovieResponse> findSimilar(UUID movieId, int limit) {
        movieSyncService.ensureSynced();
        MovieDto movie = movieClient.getMovieById(movieId);
        String query = movie.title()
                + (movie.description() != null ? ". " + movie.description() : "");

        return vectorStore.similaritySearch(
                        SearchRequest.builder().query(query).topK(limit + 1).build())
                .stream()
                .filter(doc -> !doc.getId().equals(movieId.toString()))
                .limit(limit)
                .map(doc -> new SimilarMovieResponse(
                        UUID.fromString(doc.getId()),
                        String.valueOf(doc.getMetadata().getOrDefault("title", "Unknown")),
                        doc.getScore()))
                .toList();
    }

    /**
     * @param habits          natural-language summary of usual booking time, or null
     * @param watchedMovieIds movie ids (as strings, matching Document ids) the user has reservations for
     */
    private record UserProfile(String habits, Set<String> watchedMovieIds) {
        static final UserProfile EMPTY = new UserProfile(null, Set.of());
    }

    private UserProfile buildUserProfile(UUID userId) {
        try {
            List<ReservationDto> reservations = reservationClient.getByUserId(userId);
            if (reservations == null || reservations.isEmpty()) {
                return UserProfile.EMPTY;
            }
            List<ShowDto> shows = reservations.stream()
                    .filter(r -> !"CANCELLED".equalsIgnoreCase(r.status()))
                    .sorted(Comparator.comparing(ReservationDto::createdAt,
                            Comparator.nullsLast(Comparator.reverseOrder())))
                    .limit(MAX_HISTORY_SAMPLE)
                    .map(reservation -> {
                        try {
                            return showClient.getShowById(reservation.showId());
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .toList();
            if (shows.isEmpty()) {
                return UserProfile.EMPTY;
            }

            Set<String> watchedMovieIds = shows.stream()
                    .map(show -> show.movieId().toString())
                    .collect(Collectors.toSet());

            List<LocalDateTime> showTimes = shows.stream()
                    .map(ShowDto::startTime)
                    .filter(Objects::nonNull)
                    .toList();
            String habits = null;
            if (!showTimes.isEmpty()) {
                int usualHour = mostFrequent(showTimes, t -> t.getHour());
                DayOfWeek usualDay = mostFrequent(showTimes, LocalDateTime::getDayOfWeek);
                habits = "User booking habits (from %d past reservations): they usually watch movies around %02d:00, most often on %s."
                        .formatted(showTimes.size(), usualHour, usualDay);
            }
            return new UserProfile(habits, watchedMovieIds);
        } catch (Exception e) {
            log.warn("Could not build profile for user {}: {}", userId, e.getMessage());
            return UserProfile.EMPTY;
        }
    }

    private String buildUpcomingShows(List<Document> candidates, MovieDto currentMovie) {
        try {
            Map<String, String> titlesById = new HashMap<>();
            candidates.forEach(doc -> titlesById.put(doc.getId(),
                    String.valueOf(doc.getMetadata().getOrDefault("title", "Unknown"))));
            if (currentMovie != null) {
                titlesById.put(currentMovie.id().toString(), currentMovie.title());
            }
            if (titlesById.isEmpty()) {
                return null;
            }

            LocalDateTime now = LocalDateTime.now();
            List<ShowDto> upcoming = showClient.getAllShows().stream()
                    .filter(show -> show.startTime() != null && show.startTime().isAfter(now))
                    .filter(show -> titlesById.containsKey(show.movieId().toString()))
                    .sorted(Comparator.comparing(ShowDto::startTime))
                    .limit(MAX_UPCOMING_SHOWS)
                    .toList();
            if (upcoming.isEmpty()) {
                return null;
            }

            StringBuilder text = new StringBuilder("Upcoming showtimes for those movies:\n");
            for (ShowDto show : upcoming) {
                text.append("- ").append(titlesById.get(show.movieId().toString()))
                        .append(": ").append(show.startTime().format(SHOW_TIME))
                        .append(", ").append(show.price()).append(" EGP\n");
            }
            return text.toString();
        } catch (Exception e) {
            log.warn("Could not fetch upcoming shows: {}", e.getMessage());
            return null;
        }
    }

    private String describe(MovieDto movie) {
        StringBuilder text = new StringBuilder("Title: ").append(movie.title());
        if (movie.genre() != null && !movie.genre().isBlank()) {
            text.append(" | Genre: ").append(movie.genre());
        }
        if (movie.description() != null && !movie.description().isBlank()) {
            text.append(" | Description: ").append(movie.description());
        }
        if (movie.durationMinutes() != null) {
            text.append(" | Duration: ").append(movie.durationMinutes()).append(" minutes");
        }
        if (movie.releaseDate() != null) {
            text.append(" | Released: ").append(movie.releaseDate());
        }
        return text.toString();
    }

    private <T> T mostFrequent(List<LocalDateTime> times, Function<LocalDateTime, T> classifier) {
        return times.stream()
                .collect(Collectors.groupingBy(classifier, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow();
    }
}
