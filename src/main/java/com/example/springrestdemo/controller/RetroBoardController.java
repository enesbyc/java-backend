package com.example.springrestdemo.controller;

import com.example.springrestdemo.dto.AddRetroCardEmojiRequest;
import com.example.springrestdemo.dto.CreateRetroBoardRequest;
import com.example.springrestdemo.dto.CreateRetroCardRequest;
import com.example.springrestdemo.dto.PatchRetroCardRequest;
import com.example.springrestdemo.dto.SetRetroCardVoteRequest;
import com.example.springrestdemo.dto.RetroBoardCreatedResponse;
import com.example.springrestdemo.dto.RetroBoardStateResponse;
import com.example.springrestdemo.dto.RetroCardDto;
import com.example.springrestdemo.service.RetroBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/retro/boards", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "RetroBoard", description = "Zamanlı retro panosu")
public class RetroBoardController {

    private final RetroBoardService retroBoardService;

    public RetroBoardController(RetroBoardService retroBoardService) {
        this.retroBoardService = retroBoardService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retro panosu oluştur", description = "İki sütun ve süre ile pano açar.")
    public RetroBoardCreatedResponse create(@Valid @RequestBody CreateRetroBoardRequest body) {
        return retroBoardService.createBoard(body);
    }

    @GetMapping("/{boardId}")
    @Operation(summary = "Pano durumu", description = "viewerId ile süre dolmadan yalnızca kendi kartlarının metni görünür.")
    public RetroBoardStateResponse get(
            @PathVariable String boardId, @RequestParam(name = "viewerId", required = false) String viewerId) {
        return retroBoardService.getBoard(boardId, viewerId);
    }

    @PostMapping(path = "/{boardId}/cards", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Kart ekle")
    public RetroCardDto addCard(@PathVariable String boardId, @Valid @RequestBody CreateRetroCardRequest body) {
        return retroBoardService.addCard(boardId, body);
    }

    @PatchMapping(path = "/{boardId}/cards/{cardId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Kartın grubunu güncelle")
    public RetroCardDto patchCard(
            @PathVariable String boardId,
            @PathVariable String cardId,
            @RequestParam(name = "viewerId", required = false) String viewerId,
            @Valid @RequestBody PatchRetroCardRequest body) {
        return retroBoardService.patchCard(boardId, cardId, body, viewerId);
    }

    @PostMapping(path = "/{boardId}/cards/{cardId}/emojis", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Kart emoji ekle", description = "Kartlar herkese açıldıktan sonra; sınırsız.")
    public RetroCardDto addEmoji(
            @PathVariable String boardId,
            @PathVariable String cardId,
            @Valid @RequestBody AddRetroCardEmojiRequest body) {
        return retroBoardService.addEmoji(boardId, cardId, body);
    }

    @PostMapping(path = "/{boardId}/cards/{cardId}/votes", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Oy ver / güncelle", description = "Katılıyorum veya katılmıyorum; panoda kişi başına en fazla 3 oy.")
    public RetroCardDto setVote(
            @PathVariable String boardId,
            @PathVariable String cardId,
            @Valid @RequestBody SetRetroCardVoteRequest body) {
        return retroBoardService.setVote(boardId, cardId, body);
    }

    @DeleteMapping("/{boardId}/cards/{cardId}/votes")
    @Operation(summary = "Oyu kaldır", description = "Oy hakkını başka karta taşımak için.")
    public RetroCardDto removeVote(
            @PathVariable String boardId,
            @PathVariable String cardId,
            @RequestParam(name = "personId") String personId) {
        return retroBoardService.removeVote(boardId, cardId, personId);
    }

    @PostMapping("/{boardId}/end-collecting")
    @Operation(summary = "Veri girişini lead bitirir", description = "Süreyi beklemeden tüm kartları açar.")
    public RetroBoardStateResponse endCollecting(
            @PathVariable String boardId, @RequestParam(name = "actorId") String actorId) {
        return retroBoardService.endCollectingEarly(boardId, actorId);
    }

    @PostMapping("/{boardId}/synthesize-ai")
    @Operation(
            summary = "Gemini ile grupla ve sırala",
            description = "Lead; kart metinleri ve oyları Gemini’ye gönderir, sonuç herkese görünür olur (asenkron; RUNNING iken yüklenir).")
    public RetroBoardStateResponse synthesizeAi(
            @PathVariable String boardId, @RequestParam(name = "actorId") String actorId) {
        return retroBoardService.requestAiSynthesis(boardId, actorId);
    }
}
