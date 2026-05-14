package com.example.springrestdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Retro oluşturuldu")
public record RetroBoardCreatedResponse(
        @Schema(example = "rb_a1b2c3") String boardId,
        String endsAt,
        List<RetroGroupDto> groups) {}
