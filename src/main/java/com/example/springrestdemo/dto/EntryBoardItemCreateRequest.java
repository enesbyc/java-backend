package com.example.springrestdemo.dto;

import com.example.springrestdemo.entity.EntryBoardColumn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Giriş panosu kartı oluşturma")
public record EntryBoardItemCreateRequest(
        @Schema(description = "Pano kimliği", example = "default")
        @Size(max = 64)
        String boardId,
        @NotNull @Schema(description = "good = İyi gidenler, improve = Geliştirilmesi gerekenler", example = "good")
        EntryBoardColumn column,
        @NotBlank @Size(max = 2000) @Schema(description = "Kart metni") String content,
        @Size(max = 256) @Schema(description = "İsim (yalnızca isim göster seçiliyse kaydedilir)") String authorName,
        @Schema(description = "İsmin kartta görünmesi") boolean showAuthorName) {}
