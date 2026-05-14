package com.example.springrestdemo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "retro_card_emojis")
public class RetroCardEmojiEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "card_id", nullable = false, length = 64)
    private String cardId;

    @Column(name = "person_id", nullable = false, length = 64)
    private String personId;

    @Column(nullable = false, length = 32)
    private String emoji;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected RetroCardEmojiEntity() {}

    public RetroCardEmojiEntity(String id, String cardId, String personId, String emoji, Instant createdAt) {
        this.id = id;
        this.cardId = cardId;
        this.personId = personId;
        this.emoji = emoji;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getCardId() {
        return cardId;
    }

    public String getPersonId() {
        return personId;
    }

    public String getEmoji() {
        return emoji;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
