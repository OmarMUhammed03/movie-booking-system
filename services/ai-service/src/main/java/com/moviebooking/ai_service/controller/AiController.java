package com.moviebooking.ai_service.controller;

import com.moviebooking.ai_service.dto.request.ChatRequest;
import com.moviebooking.ai_service.dto.response.ChatResponse;
import com.moviebooking.ai_service.dto.response.SimilarMovieResponse;
import com.moviebooking.ai_service.dto.response.SyncResponse;
import com.moviebooking.ai_service.service.ChatService;
import com.moviebooking.ai_service.service.MovieSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI Controller", description = "AI chatbot for movie recommendations backed by Ollama and pgvector")
public class AiController {

    private final ChatService chatService;
    private final MovieSyncService movieSyncService;

    @PostMapping("/chat")
    @Operation(summary = "Chat with the movie assistant",
            description = "Sends a natural-language message. Optionally pass userId (personalized timing suggestions from "
                    + "reservation history), movieId (answers grounded in the movie currently being viewed) and "
                    + "conversationId (continues a previous conversation).")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.chat(request));
    }

    @GetMapping("/movies/{movieId}/similar")
    @Operation(summary = "More like this",
            description = "Returns movies semantically similar to the given movie, ranked by embedding similarity.")
    public ResponseEntity<List<SimilarMovieResponse>> similar(
            @PathVariable UUID movieId,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(chatService.findSimilar(movieId, limit));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/sync")
    @Operation(summary = "Embed the movie catalogue",
            description = "Fetches all movies from movie-service and embeds their descriptions into the vector store. "
                    + "Movies already embedded are skipped unless force=true. Restricted to administrators.")
    public ResponseEntity<SyncResponse> sync(@RequestParam(defaultValue = "false") boolean force) {
        return ResponseEntity.ok(movieSyncService.sync(force));
    }
}
