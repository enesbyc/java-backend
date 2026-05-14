package com.example.springrestdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Kişi bilgisi")
public record PersonResponse(
        @Schema(description = "Ön yüz anahtarı", example = "p1")
        String id,
        @Schema(description = "Ad", example = "Ahmet")
        String name,
        @Schema(description = "Soyad", example = "Yılmaz")
        String surname
) {}
