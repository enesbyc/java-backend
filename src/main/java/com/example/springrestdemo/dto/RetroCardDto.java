package com.example.springrestdemo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "Retro kartı (maskeleme süre bitene kadar)")
public record RetroCardDto(
        String id,
        String groupId,
        String authorId,
        @Schema(description = "Süre dolmadan başkasının kartı boş; masked=true") String content,
        boolean masked,
        String createdAt,
        @Schema(description = "false ise isim yerine **") boolean showAuthorName,
        @Schema(description = "emoji → tekrar sayısı") Map<String, Long> emojiCounts,
        @Schema(description = "Katılıyorum sayısı") long agreeCount,
        @Schema(description = "Katılmıyorum sayısı") long disagreeCount,
        @Schema(description = "viewerId için bu karttaki oy: AGREE, DISAGREE veya null") String myVote) {}
