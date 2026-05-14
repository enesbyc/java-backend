package com.example.springrestdemo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "retro_cards")
public class RetroCardEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "board_id", nullable = false, length = 64)
    private String boardId;

    @Column(name = "group_id", length = 96)
    private String groupId;

    @Column(nullable = false, length = 4096)
    private String content;

    @Column(name = "author_id", nullable = false, length = 64)
    private String authorId;

    @Column(name = "show_author_name", nullable = false)
    private boolean showAuthorName;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected RetroCardEntity() {}

    public RetroCardEntity(
            String id,
            String boardId,
            String groupId,
            String content,
            String authorId,
            boolean showAuthorName,
            Instant createdAt) {
        this.id = id;
        this.boardId = boardId;
        this.groupId = groupId;
        this.content = content;
        this.authorId = authorId;
        this.showAuthorName = showAuthorName;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getBoardId() {
        return boardId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getContent() {
        return content;
    }

    public String getAuthorId() {
        return authorId;
    }

    public boolean isShowAuthorName() {
        return showAuthorName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
