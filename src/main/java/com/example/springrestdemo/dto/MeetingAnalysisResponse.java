package com.example.springrestdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Gemini toplantı analizi sonucu")
public record MeetingAnalysisResponse(
        @Schema(description = "Markdown analiz metni") String analysis,
        @Schema(description = "Kullanılan model") String model,
        Integer promptTokens,
        Integer completionTokens,
        Integer totalTokens,
        BigDecimal estimatedCostUsd) {}
