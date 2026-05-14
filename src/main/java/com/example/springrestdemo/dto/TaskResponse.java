package com.example.springrestdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Görev")
public record TaskResponse(
        @Schema(example = "t1") String id,
        @Schema(example = "API Entegrasyonu") String title,
        String description,
        @Schema(example = "p1") String assigneeId,
        @Schema(example = "in-progress", allowableValues = {"todo", "in-progress", "done"})
        String status,
        @Schema(example = "5") int storyPoint,
        @Schema(example = "4") Integer qualityScore) {}
