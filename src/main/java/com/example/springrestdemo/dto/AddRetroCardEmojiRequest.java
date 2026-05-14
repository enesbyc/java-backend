package com.example.springrestdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Retro kartına emoji ekle (sınırsız)")
public record AddRetroCardEmojiRequest(
        @NotBlank @Size(max = 64) String personId,
        @NotBlank @Size(max = 32) String emoji) {}
