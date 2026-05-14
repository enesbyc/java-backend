package com.example.springrestdemo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Görev kısmi güncelleme")
public record TaskPatchRequest(
        @Schema(example = "done") String status,
        @Schema(example = "4") Integer qualityScore) {}
