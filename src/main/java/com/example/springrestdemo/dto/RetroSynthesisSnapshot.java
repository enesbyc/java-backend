package com.example.springrestdemo.dto;

import java.util.List;

/** Gemini’ye gidecek kart + oy özeti (read-only snapshot). */
public record RetroSynthesisSnapshot(
        String boardId,
        List<RetroSynthesisCardInput> cards) {

    public record RetroSynthesisCardInput(
            String cardId, String content, long agreeCount, long disagreeCount, long netScore) {}
}
