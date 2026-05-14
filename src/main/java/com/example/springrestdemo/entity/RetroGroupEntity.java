package com.example.springrestdemo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "retro_groups")
public class RetroGroupEntity {

    @Id
    @Column(length = 96)
    private String id;

    @Column(name = "board_id", nullable = false, length = 64)
    private String boardId;

    @Column(nullable = false, length = 256)
    private String title;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    protected RetroGroupEntity() {}

    public RetroGroupEntity(String id, String boardId, String title, int sortOrder) {
        this.id = id;
        this.boardId = boardId;
        this.title = title;
        this.sortOrder = sortOrder;
    }

    public String getId() {
        return id;
    }

    public String getBoardId() {
        return boardId;
    }

    public String getTitle() {
        return title;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
