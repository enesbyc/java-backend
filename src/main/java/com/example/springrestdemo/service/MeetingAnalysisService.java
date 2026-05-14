package com.example.springrestdemo.service;

import com.example.springrestdemo.dto.MeetingAnalysisResponse;
import org.springframework.stereotype.Service;

@Service
public class MeetingAnalysisService {

    private final GeminiService geminiService;

    public MeetingAnalysisService(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    public MeetingAnalysisResponse analyze(String notes) {
        String prompt = buildPrompt(notes.trim());
        String analysis = geminiService.generateContent(prompt);
        String usedModel = geminiService.getEffectiveModelId();
        GeminiService.TokenUsage u = geminiService.getLastUsage();
        if (u == null) {
            return new MeetingAnalysisResponse(analysis, usedModel, null, null, null, null);
        }
        return new MeetingAnalysisResponse(
                analysis,
                usedModel,
                u.promptTokens(),
                u.completionTokens(),
                u.totalTokens(),
                u.estimatedCostUsd());
    }

    private static String buildPrompt(String notes) {
        return """
                Sen deneyimli bir ürün ve teknik lidersin. Aşağıda bir toplantının ham notları veya transkripti var.

                Görevin:
                1) Notlardan net **problemleri** ve **belirsizlikleri** maddeler halinde çıkar.
                2) Her problem için **uygulanabilir çözüm önerileri** ver (kısa, eylem odaklı; mümkünse sahip/son tarih öner).
                3) Varsa **riskler**, **bağımlılıklar** ve **takip sorularını** ayrı bir bölümde listele.
                4) Önceliklendirme için basit bir **önerilen sıra** (yüksek/orta/düşük) ver.

                Yanıtını **Türkçe** yaz. Markdown başlıkları (##, ###) ve madde işaretleri kullan.

                --- TOPLANTI NOTLARI ---
                """
                + notes
                + """

                --- SON ---
                """;
    }
}
