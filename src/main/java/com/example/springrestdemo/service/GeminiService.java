package com.example.springrestdemo.service;

import com.example.springrestdemo.exception.GeminiQuotaExceededException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

/**
 * Google Gemini REST API — generateContent. Model sabit: {@value #MODEL_ID} (yeni hesaplarda 2.0-flash kapalı).
 * 503 / geçici 429 için üstel geri çekilme ile yeniden dener.
 */
@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    /** Google: eski {@code gemini-2.0-flash} yeni hesaplarda kapalı; güncel Flash. */
    private static final String MODEL_ID = "gemini-2.5-flash";

    private static final int MAX_ATTEMPTS = 6;
    private static final long BASE_BACKOFF_MS = 1500L;
    private static final long MAX_BACKOFF_MS = 22_000L;

    private static final BigDecimal INPUT_COST_PER_MILLION = new BigDecimal("0.10");
    private static final BigDecimal OUTPUT_COST_PER_MILLION = new BigDecimal("0.40");

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.base-url:https://generativelanguage.googleapis.com/v1beta}")
    private String baseUrl;

    @Value("${gemini.api.temperature:0.3}")
    private double temperature;

    @Value("${gemini.api.max-tokens:8192}")
    private int maxOutputTokens;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    private final AtomicLong totalInputTokens = new AtomicLong(0);
    private final AtomicLong totalOutputTokens = new AtomicLong(0);
    private final AtomicInteger totalRequests = new AtomicInteger(0);

    private volatile TokenUsage lastUsage;

    public GeminiService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public record TokenUsage(
            int promptTokens, int completionTokens, int totalTokens, BigDecimal estimatedCostUsd) {}

    public TokenUsage getLastUsage() {
        return lastUsage;
    }

    public String getEffectiveModelId() {
        return MODEL_ID;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }

    public String generateContent(String prompt) {
        if (!isConfigured()) {
            throw new IllegalStateException(
                    "Gemini API anahtarı tanımlı değil. GEMINI_API_KEY ortam değişkeni veya gemini.api.key ayarlayın.");
        }
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                return generateContentOnce(prompt);
            } catch (RestClientResponseException e) {
                int code = e.getStatusCode().value();
                boolean overload = code == 503 || code == 429;
                if (overload && attempt < MAX_ATTEMPTS) {
                    long backoff =
                            Math.min(BASE_BACKOFF_MS * (1L << (attempt - 1)), MAX_BACKOFF_MS)
                                    + ThreadLocalRandom.current().nextLong(0, 1000);
                    log.warn(
                            "Gemini {} (model={}) — yoğunluk/kısıt, {} ms sonra yeniden denenecek ({}/{})",
                            code,
                            MODEL_ID,
                            backoff,
                            attempt,
                            MAX_ATTEMPTS);
                    sleepQuietly(backoff);
                    continue;
                }
                if (code == 429) {
                    log.warn("Gemini 429 quota/rate: model={} (denemeler tükendi)", MODEL_ID);
                    throw new GeminiQuotaExceededException(
                            "Gemini 429: kotanız veya istek hızı sınırı aşıldı. Bir süre bekleyip tekrar deneyin. "
                                    + "https://ai.google.dev/gemini-api/docs/rate-limits",
                            e);
                }
                String body = e.getResponseBodyAsString();
                String snippet = body != null && body.length() > 400 ? body.substring(0, 400) + "…" : body;
                log.error("Gemini HTTP {} model={}: {}", e.getStatusCode().value(), MODEL_ID, snippet);
                throw new RuntimeException(
                        "Gemini API hatası: " + e.getStatusCode().value() + " — " + snippet, e);
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                log.error("Gemini API hatası: {}", e.getMessage());
                throw new RuntimeException("Gemini API hatası: " + e.getMessage(), e);
            }
        }
        throw new IllegalStateException("Gemini: beklenmeyen durum");
    }

    private static void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Gemini çağrısı kesildi", ie);
        }
    }

    private String generateContentOnce(String prompt) {
        String url =
                String.format(
                        "%s/models/%s:generateContent?key=%s",
                        baseUrl.trim(), MODEL_ID, apiKey.trim());

        Map<String, Object> requestBody =
                Map.of(
                        "contents",
                        List.of(Map.of("parts", List.of(Map.of("text", prompt)))),
                        "generationConfig",
                        Map.of(
                                "temperature", temperature,
                                "maxOutputTokens", maxOutputTokens,
                                "topP", 0.95,
                                "topK", 40),
                        "safetySettings",
                        List.of(
                                Map.of("category", "HARM_CATEGORY_HARASSMENT", "threshold", "BLOCK_NONE"),
                                Map.of("category", "HARM_CATEGORY_HATE_SPEECH", "threshold", "BLOCK_NONE"),
                                Map.of("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold", "BLOCK_NONE"),
                                Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold", "BLOCK_NONE")));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        log.debug("Gemini generateContent model={}", MODEL_ID);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return extractTextFromResponse(response.getBody());
    }

    private String extractTextFromResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            if (root.has("error")) {
                String msg = root.path("error").path("message").asText("Unknown error");
                throw new RuntimeException("Gemini API error: " + msg);
            }
            extractTokenUsage(root);

            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode parts = candidates.get(0).path("content").path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (JsonNode p : parts) {
                        sb.append(p.path("text").asText(""));
                    }
                    return sb.toString();
                }
            }
            log.warn("Beklenmeyen Gemini yanıtı");
            return "";
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Gemini yanıtı işlenemedi: " + e.getMessage(), e);
        }
    }

    private void extractTokenUsage(JsonNode root) {
        JsonNode usage = root.path("usageMetadata");
        if (usage.isMissingNode()) {
            return;
        }
        int promptTokens = usage.path("promptTokenCount").asInt(0);
        int completionTokens = usage.path("candidatesTokenCount").asInt(0);
        int total = usage.path("totalTokenCount").asInt(0);
        totalInputTokens.addAndGet(promptTokens);
        totalOutputTokens.addAndGet(completionTokens);
        totalRequests.incrementAndGet();
        BigDecimal cost = calculateCost(promptTokens, completionTokens);
        lastUsage = new TokenUsage(promptTokens, completionTokens, total, cost);
        log.info("Gemini token — girdi: {}, çıktı: {}, toplam: {}, tahmini USD: {}", promptTokens, completionTokens, total, cost);
    }

    private static BigDecimal calculateCost(int inputTokens, int outputTokens) {
        BigDecimal in =
                INPUT_COST_PER_MILLION
                        .multiply(BigDecimal.valueOf(inputTokens))
                        .divide(BigDecimal.valueOf(1_000_000), 6, RoundingMode.HALF_UP);
        BigDecimal out =
                OUTPUT_COST_PER_MILLION
                        .multiply(BigDecimal.valueOf(outputTokens))
                        .divide(BigDecimal.valueOf(1_000_000), 6, RoundingMode.HALF_UP);
        return in.add(out).setScale(6, RoundingMode.HALF_UP);
    }
}
