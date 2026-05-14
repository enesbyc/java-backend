package com.example.springrestdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Demo giriş")
public record LoginRequest(
        @NotBlank @Size(min = 2, max = 128) @Schema(example = "ahmet.yilmaz") String username,
        @Schema(description = "Demo — doğrulanmaz", example = "demo") String password) {}
