package com.example.springrestdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Kişi bilgisi")
public record PersonResponse(
        @Schema(description = "Ön yüz anahtarı", example = "u1")
        String id,
        @Schema(description = "Ad", example = "U1")
        String name,
        @Schema(description = "Soyad", example = "")
        String surname,
        @Schema(description = "Rol (veritabanı)", example = "user")
        String role) {}
