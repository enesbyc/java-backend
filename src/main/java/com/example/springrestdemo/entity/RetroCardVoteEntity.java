package com.example.springrestdemo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(
        name = "retro_card_votes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"card_id", "person_id"}))
public class RetroCardVoteEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "card_id", nullable = false, length = 64)
    private String cardId;

    @Column(name = "person_id", nullable = false, length = 64)
    private String personId;

    @Enumerated(EnumType.STRING)
    @Column(name = "vote_type", nullable = false, length = 16)
    private RetroVoteType voteType;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected RetroCardVoteEntity() {}

    public RetroCardVoteEntity(String id, String cardId, String personId, RetroVoteType voteType, Instant createdAt) {
        this.id = id;
        this.cardId = cardId;
        this.personId = personId;
        this.voteType = voteType;
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

    public RetroVoteType getVoteType() {
        return voteType;
    }

    public void setVoteType(RetroVoteType voteType) {
        this.voteType = voteType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
