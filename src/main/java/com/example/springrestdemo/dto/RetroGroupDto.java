package com.example.springrestdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Retro sütunu")
public record RetroGroupDto(@Schema(example = "rb_x_g1") String id, String title) {}
