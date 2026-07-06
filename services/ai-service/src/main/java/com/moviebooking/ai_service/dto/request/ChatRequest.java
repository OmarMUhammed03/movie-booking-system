package com.moviebooking.ai_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record ChatRequest(
        @NotBlank(message = "Message is required")
        @Schema(description = "The user's natural-language message",
                example = "I want something scary for the weekend")
        String message,

        @Schema(description = "Optional. Enables personalized timing suggestions from this user's reservation "
                + "history (sent by the client, same trust model as reservation-service). Remove if not needed.",
                example = "44444444-4444-4444-4444-444444444401", nullable = true)
        UUID userId,

        @Schema(description = "Optional. The movie the user is currently viewing; grounds the answer in it "
                + "(\"more like this\"). Remove if not needed.",
                example = "11111111-1111-1111-1111-111111111102", nullable = true)
        UUID movieId,

        @Schema(description = "Leave empty on the first message. To continue the same conversation, "
                + "copy the conversationId returned by the previous reply.",
                example = "", nullable = true)
        String conversationId) {
}
