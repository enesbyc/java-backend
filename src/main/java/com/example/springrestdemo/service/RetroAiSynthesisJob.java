package com.example.springrestdemo.service;

import com.example.springrestdemo.dto.RetroSynthesisSnapshot;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class RetroAiSynthesisJob {

    private static final Logger log = LoggerFactory.getLogger(RetroAiSynthesisJob.class);

    private final RetroSynthesisTxHelper txHelper;
    private final GeminiService gemini;
    private final ObjectMapper objectMapper;

    public RetroAiSynthesisJob(RetroSynthesisTxHelper txHelper, GeminiService gemini, ObjectMapper objectMapper) {
        this.txHelper = txHelper;
        this.gemini = gemini;
        this.objectMapper = objectMapper;
    }

    @Async
    public void runAsync(String boardId) {
        try {
            RetroSynthesisSnapshot snap = txHelper.loadSnapshot(boardId);
            if (snap.cards().isEmpty()) {
                ObjectNode root = objectMapper.createObjectNode();
                ArrayNode groups = root.putArray("groups");
                ObjectNode g = groups.addObject();
                g.put("title", "Kart yok");
                g.putArray("items");
                txHelper.markDone(boardId, objectMapper.writeValueAsString(root));
                return;
            }
            if (!gemini.isConfigured()) {
                txHelper.markFailed(boardId, "Gemini API anahtarı tanımlı değil (GEMINI_API_KEY / gemini.api.key).");
                return;
            }
            String prompt = buildPrompt(snap);
            String raw = gemini.generateContent(prompt);
            String json = stripMarkdownFence(raw.trim());
            String validated = validateOrFallback(json, snap);
            txHelper.markDone(boardId, validated);
        } catch (Exception e) {
            log.warn("Retro AI synthesis failed boardId={}", boardId, e);
            txHelper.markFailed(boardId, e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
        }
    }

    private String buildPrompt(RetroSynthesisSnapshot snap) throws Exception {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (RetroSynthesisSnapshot.RetroSynthesisCardInput c : snap.cards()) {
            rows.add(
                    Map.of(
                            "cardId",
                            c.cardId(),
                            "content",
                            c.content(),
                            "agreeCount",
                            c.agreeCount(),
                            "disagreeCount",
                            c.disagreeCount(),
                            "netScore",
                            c.netScore()));
        }
        String inputJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rows);
        return """
                Sen deneyimli bir agile retro moderatörüsün. Aşağıdaki JSON dizisi retro panosundaki kartlardır.
                Her nesne: cardId, content, agreeCount (katılıyorum), disagreeCount (katılmıyorum), netScore (agreeCount - disagreeCount).

                Görevin:
                1) Kartları anlamlı tematik gruplara ayır (genelde 2–5 grup; tek grup sadece gerçekten tek tema varsa).
                2) Her grup içindeki "items" listesini netScore DEĞERİNE GÖRE azalan sırada sırala (en yüksek netScore en üstte).
                3) Çıktın SADECE geçerli JSON olmalı; açıklama, markdown kod çiti veya metin dışı karakter olmasın.
                4) Şema tam olarak şöyle olsun:
                {"groups":[{"title":"string","items":[{"cardId":"string","content":"string","agreeCount":number,"disagreeCount":number}]}]}
                5) Her cardId, girdideki id ile birebir aynı olmalı. Hiçbir kart eksik veya iki kez kullanılmamalı.

                Girdi kartları (JSON):
                """
                + inputJson;
    }

    private static String stripMarkdownFence(String raw) {
        String s = raw;
        if (s.startsWith("```")) {
            int nl = s.indexOf('\n');
            if (nl > 0) {
                s = s.substring(nl + 1);
            }
            int end = s.lastIndexOf("```");
            if (end > 0) {
                s = s.substring(0, end).trim();
            }
        }
        return s.trim();
    }

    private String validateOrFallback(String json, RetroSynthesisSnapshot snap) throws Exception {
        Set<String> expected = snap.cards().stream()
                .map(RetroSynthesisSnapshot.RetroSynthesisCardInput::cardId)
                .collect(Collectors.toSet());
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode groups = root.path("groups");
            if (!groups.isArray() || groups.isEmpty()) {
                return fallbackJson(snap);
            }
            Set<String> seen = new HashSet<>();
            for (JsonNode g : groups) {
                JsonNode items = g.path("items");
                if (!items.isArray()) {
                    return fallbackJson(snap);
                }
                for (JsonNode it : items) {
                    String id = it.path("cardId").asText(null);
                    if (id == null || id.isBlank() || !expected.contains(id) || seen.contains(id)) {
                        return fallbackJson(snap);
                    }
                    seen.add(id);
                }
            }
            if (!seen.equals(expected)) {
                return fallbackJson(snap);
            }
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            log.debug("Gemini JSON doğrulanamadı, yedek sıralama kullanılıyor: {}", e.getMessage());
            return fallbackJson(snap);
        }
    }

    private String fallbackJson(RetroSynthesisSnapshot snap) throws Exception {
        List<RetroSynthesisSnapshot.RetroSynthesisCardInput> sorted =
                new ArrayList<>(snap.cards());
        sorted.sort(Comparator.comparingLong(RetroSynthesisSnapshot.RetroSynthesisCardInput::netScore).reversed());
        ObjectNode root = objectMapper.createObjectNode();
        ArrayNode groups = root.putArray("groups");
        ObjectNode g = groups.addObject();
        g.put("title", "Tüm maddeler (oy skoruna göre)");
        ArrayNode items = g.putArray("items");
        for (RetroSynthesisSnapshot.RetroSynthesisCardInput c : sorted) {
            ObjectNode o = items.addObject();
            o.put("cardId", c.cardId());
            o.put("content", c.content());
            o.put("agreeCount", c.agreeCount());
            o.put("disagreeCount", c.disagreeCount());
        }
        return objectMapper.writeValueAsString(root);
    }
}
