package com.example.springrestdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Toplantı notları")
public record MeetingNotesRequest(
        @NotBlank @Size(max = 50_000) @Schema(description = "Ham toplantı notları / transkript", example = "Sprint: API gecikmesi...")
                String notes) {}
