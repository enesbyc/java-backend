package com.example.springrestdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Retro panosu başlatma")
public record CreateRetroBoardRequest(
        @NotNull @Min(1) @Max(480) @Schema(description = "Süre (dakika)", example = "10") Integer durationMinutes) {}
