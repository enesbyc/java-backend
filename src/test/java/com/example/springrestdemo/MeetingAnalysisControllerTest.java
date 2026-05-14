package com.example.springrestdemo;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.springrestdemo.controller.MeetingAnalysisController;
import com.example.springrestdemo.dto.MeetingAnalysisResponse;
import com.example.springrestdemo.service.GeminiService;
import com.example.springrestdemo.service.MeetingAnalysisService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MeetingAnalysisController.class)
class MeetingAnalysisControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MeetingAnalysisService meetingAnalysisService;

    @MockBean
    private GeminiService geminiService;

    @Test
    void analyze_returnsAnalysisWhenGeminiConfigured() throws Exception {
        when(geminiService.isConfigured()).thenReturn(true);
        when(meetingAnalysisService.analyze(anyString()))
                .thenReturn(
                        new MeetingAnalysisResponse(
                                "## Özet\n- Test",
                                "gemini-2.5-flash",
                                100,
                                50,
                                150,
                                new BigDecimal("0.000030")));

        mockMvc.perform(post("/api/meetings/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"notes\":\"Bugün deployment sorunu konuştuk.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.analysis").value("## Özet\n- Test"))
                .andExpect(jsonPath("$.model").value("gemini-2.5-flash"))
                .andExpect(jsonPath("$.promptTokens").value(100));
    }

    @Test
    void analyze_returns503WhenNotConfigured() throws Exception {
        when(geminiService.isConfigured()).thenReturn(false);

        mockMvc.perform(post("/api/meetings/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"notes\":\"x\"}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").exists());
    }
}
