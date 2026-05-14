package com.example.springrestdemo.service;

import com.example.springrestdemo.dto.EntryBoardItemCreateRequest;
import com.example.springrestdemo.dto.EntryBoardItemResponse;
import com.example.springrestdemo.entity.EntryBoardItemEntity;
import com.example.springrestdemo.repository.EntryBoardItemRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EntryBoardService {

    private static final String DEFAULT_BOARD_ID = "default";

    private final EntryBoardItemRepository repository;

    public EntryBoardService(EntryBoardItemRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<EntryBoardItemResponse> listItems(String boardId) {
        String bid = boardId == null || boardId.isBlank() ? DEFAULT_BOARD_ID : boardId.trim();
        return repository.findByBoardIdOrderByCreatedAtAsc(bid).stream().map(this::toResponse).toList();
    }

    @Transactional
    public EntryBoardItemResponse create(EntryBoardItemCreateRequest req) {
        String bid = req.boardId() == null || req.boardId().isBlank() ? DEFAULT_BOARD_ID : req.boardId().trim();
        String rawName = req.authorName() == null ? "" : req.authorName().trim();
        boolean show = req.showAuthorName() && !rawName.isEmpty();
        String name = show ? rawName : null;
        Instant now = Instant.now();
        var entity = new EntryBoardItemEntity(
                "eb_" + UUID.randomUUID().toString().replace("-", ""),
                bid,
                req.column(),
                req.content().trim(),
                name,
                show,
                now);
        repository.save(entity);
        return toResponse(entity);
    }

    private EntryBoardItemResponse toResponse(EntryBoardItemEntity e) {
        boolean show = e.isShowAuthorName();
        String author = show ? e.getAuthorName() : null;
        return new EntryBoardItemResponse(
                e.getId(),
                e.getBoardId(),
                e.getBoardColumn().name(),
                e.getContent(),
                show,
                author,
                e.getCreatedAt().toString());
    }
}
