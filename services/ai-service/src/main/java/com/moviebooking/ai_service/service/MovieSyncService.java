package com.moviebooking.ai_service.service;

import com.moviebooking.ai_service.client.MovieClient;
import com.moviebooking.ai_service.dto.MovieDto;
import com.moviebooking.ai_service.dto.PageDto;
import com.moviebooking.ai_service.dto.response.SyncResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Embeds the movie catalogue into the pgvector store so the chatbot can do
 * semantic similarity search over movie descriptions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MovieSyncService {

    private static final int PAGE_SIZE = 50;

    private final MovieClient movieClient;
    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;

    @Value("${ai.sync.on-startup:true}")
    private boolean syncOnStartup;

    private volatile boolean synced = false;

    /**
     * Attempted at startup. movie-service requires a JWT, so this succeeds only if
     * its GET endpoints are reachable; otherwise the catalogue is embedded lazily
     * on the first authenticated chat request (or via POST /api/ai/sync).
     */
    @EventListener(ApplicationReadyEvent.class)
    public void syncOnStartup() {
        if (!syncOnStartup) {
            return;
        }
        try {
            SyncResponse result = sync(false);
            log.info("Startup embedding sync finished: {}", result);
        } catch (Exception e) {
            log.warn("Startup embedding sync failed ({}). The catalogue will be embedded on the first "
                    + "authenticated chat request, or trigger it manually: POST /api/ai/sync as ADMIN.",
                    e.getMessage());
        }
    }

    /**
     * Called from authenticated request paths; the caller's JWT is propagated to
     * movie-service by the Feign interceptor. Never throws.
     */
    public void ensureSynced() {
        if (synced) {
            return;
        }
        synchronized (this) {
            if (synced) {
                return;
            }
            try {
                sync(false);
            } catch (Exception e) {
                log.warn("Lazy embedding sync failed: {}", e.getMessage());
            }
        }
    }

    public synchronized SyncResponse sync(boolean force) {
        Set<String> existingIds = force ? Set.of() : fetchExistingIds();
        int embedded = 0;
        int skipped = 0;
        int totalMovies = 0;
        int page = 0;

        while (true) {
            PageDto<MovieDto> moviePage = movieClient.getMovies(page, PAGE_SIZE);
            List<MovieDto> movies = moviePage.content() == null ? List.of() : moviePage.content();
            totalMovies += movies.size();

            List<Document> documents = new ArrayList<>();
            for (MovieDto movie : movies) {
                if (existingIds.contains(movie.id().toString())) {
                    skipped++;
                    continue;
                }
                documents.add(toDocument(movie));
            }
            if (!documents.isEmpty()) {
                vectorStore.add(documents);
                embedded += documents.size();
            }
            if (moviePage.last() || movies.isEmpty()) {
                break;
            }
            page++;
        }

        synced = true;
        log.info("Embedding sync done: {} embedded, {} skipped, {} total", embedded, skipped, totalMovies);
        return new SyncResponse(embedded, skipped, totalMovies);
    }

    private Set<String> fetchExistingIds() {
        try {
            return new HashSet<>(jdbcTemplate.queryForList("SELECT id::text FROM vector_store", String.class));
        } catch (DataAccessException e) {
            // table not created yet — nothing embedded so far
            return Set.of();
        }
    }

    private Document toDocument(MovieDto movie) {
        StringBuilder text = new StringBuilder("Title: ").append(movie.title());
        if (movie.genre() != null && !movie.genre().isBlank()) {
            text.append("\nGenre: ").append(movie.genre());
        }
        if (movie.description() != null && !movie.description().isBlank()) {
            text.append("\nDescription: ").append(movie.description());
        }
        if (movie.durationMinutes() != null) {
            text.append("\nDuration: ").append(movie.durationMinutes()).append(" minutes");
        }
        if (movie.releaseDate() != null) {
            text.append("\nRelease date: ").append(movie.releaseDate());
        }

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("title", movie.title());
        if (movie.genre() != null && !movie.genre().isBlank()) {
            metadata.put("genre", movie.genre());
        }
        if (movie.durationMinutes() != null) {
            metadata.put("durationMinutes", movie.durationMinutes());
        }
        if (movie.releaseDate() != null) {
            metadata.put("releaseDate", movie.releaseDate().toString());
        }

        return Document.builder()
                .id(movie.id().toString())
                .text(text.toString())
                .metadata(metadata)
                .build();
    }
}
