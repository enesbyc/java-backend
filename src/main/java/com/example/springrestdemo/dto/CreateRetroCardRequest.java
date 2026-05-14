package com.example.springrestdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Retro kartı oluştur")
public record CreateRetroCardRequest(
        @NotBlank @Size(max = 4000) String content,
        @Schema(description = "null = gruplanmamış") String groupId,
        @NotBlank @Size(max = 64) String authorId,
        @Schema(description = "İsim kartta görünsün; false ise **", defaultValue = "false")
        Boolean showAuthorName) {}
