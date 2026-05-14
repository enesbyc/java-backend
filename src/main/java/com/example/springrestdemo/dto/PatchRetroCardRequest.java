package com.example.springrestdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Retro kart grup güncelleme")
public record PatchRetroCardRequest(@Schema(description = "null = gruplanmamış") String groupId) {}
