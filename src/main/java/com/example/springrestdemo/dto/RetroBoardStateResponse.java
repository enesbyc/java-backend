package com.example.springrestdemo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Retro pano durumu")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RetroBoardStateResponse(
        String boardId,
        String startedAt,
        String endsAt,
        boolean revealed,
        @Schema(description = "viewerId ile: kullanıcının panoda kullandığı oy adedi (0–3)")
        int viewerVotesUsed,
        @Schema(description = "IDLE | RUNNING | DONE | FAILED — Gemini gruplama adımı")
        String synthesisStatus,
        @Schema(description = "DONE iken: { groups: [{ title, items: [{ cardId, content, agreeCount, disagreeCount }] }] }")
        JsonNode synthesisResult,
        @Schema(description = "FAILED iken kısa hata")
        String synthesisError,
        List<RetroGroupDto> groups,
        List<RetroCardDto> cards) {}
