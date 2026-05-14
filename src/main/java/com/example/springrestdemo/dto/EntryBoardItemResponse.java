package com.example.springrestdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Giriş panosu kartı")
public record EntryBoardItemResponse(
        @Schema(example = "eb_1") String id,
        @Schema(example = "default") String boardId,
        @Schema(example = "good") String column,
        String content,
        boolean showAuthorName,
        @Schema(description = "Anonim ise null") String authorName,
        @Schema(example = "2026-05-14T12:00:00Z") String createdAt) {}
