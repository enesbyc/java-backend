package com.example.springrestdemo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "entry_board_items")
public class EntryBoardItemEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "board_id", nullable = false, length = 64)
    private String boardId;

    @Enumerated(EnumType.STRING)
    @Column(name = "board_column", nullable = false, length = 32)
    private EntryBoardColumn boardColumn;

    @Column(nullable = false, length = 2048)
    private String content;

    @Column(name = "author_name", length = 256)
    private String authorName;

    @Column(name = "show_author_name", nullable = false)
    private boolean showAuthorName;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected EntryBoardItemEntity() {}

    public EntryBoardItemEntity(
            String id,
            String boardId,
            EntryBoardColumn boardColumn,
            String content,
            String authorName,
            boolean showAuthorName,
            Instant createdAt) {
        this.id = id;
        this.boardId = boardId;
        this.boardColumn = boardColumn;
        this.content = content;
        this.authorName = authorName;
        this.showAuthorName = showAuthorName;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getBoardId() {
        return boardId;
    }

    public EntryBoardColumn getBoardColumn() {
        return boardColumn;
    }

    public String getContent() {
        return content;
    }

    public String getAuthorName() {
        return authorName;
    }

    public boolean isShowAuthorName() {
        return showAuthorName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
