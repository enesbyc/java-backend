package com.example.springrestdemo.controller;

import com.example.springrestdemo.dto.EntryBoardItemCreateRequest;
import com.example.springrestdemo.dto.EntryBoardItemResponse;
import com.example.springrestdemo.service.EntryBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/entry-board", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "EntryBoard", description = "Giriş ekranı panosu (İyi gidenler / Geliştirilmesi gerekenler)")
public class EntryBoardController {

    private final EntryBoardService entryBoardService;

    public EntryBoardController(EntryBoardService entryBoardService) {
        this.entryBoardService = entryBoardService;
    }

    @GetMapping("/items")
    @Operation(summary = "Pano kartlarını listele")
    public List<EntryBoardItemResponse> list(@RequestParam(name = "boardId", required = false) String boardId) {
        return entryBoardService.listItems(boardId);
    }

    @PostMapping(path = "/items", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Yeni kart ekle", description = "H2 veritabanına kaydedilir.")
    public EntryBoardItemResponse create(@Valid @RequestBody EntryBoardItemCreateRequest body) {
        return entryBoardService.create(body);
    }
}
