package com.example.springrestdemo.controller;

import com.example.springrestdemo.dto.MeetingAnalysisResponse;
import com.example.springrestdemo.dto.MeetingNotesRequest;
import com.example.springrestdemo.exception.GeminiQuotaExceededException;
import com.example.springrestdemo.service.GeminiService;
import com.example.springrestdemo.service.MeetingAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/meetings", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Meeting AI", description = "Toplantı notları — Gemini analizi")
public class MeetingAnalysisController {

    private final MeetingAnalysisService meetingAnalysisService;
    private final GeminiService geminiService;

    public MeetingAnalysisController(MeetingAnalysisService meetingAnalysisService, GeminiService geminiService) {
        this.meetingAnalysisService = meetingAnalysisService;
        this.geminiService = geminiService;
    }

    @PostMapping(value = "/analyze", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Toplantı notlarını analiz et", description = "Problemleri çıkarır ve çözüm önerileri üretir (Gemini).")
    public ResponseEntity<?> analyze(@Valid @RequestBody MeetingNotesRequest request) {
        if (!geminiService.isConfigured()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(java.util.Map.of(
                            "error",
                            "Gemini yapılandırılmadı. GEMINI_API_KEY veya gemini.api.key tanımlayın."));
        }
        try {
            MeetingAnalysisResponse body = meetingAnalysisService.analyze(request.notes());
            return ResponseEntity.ok(body);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(java.util.Map.of("error", e.getMessage()));
        } catch (GeminiQuotaExceededException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(java.util.Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(java.util.Map.of("error", e.getMessage()));
        }
    }
}
