package com.example.springrestdemo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "retro_boards")
public class RetroBoardEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "ends_at", nullable = false)
    private Instant endsAt;

    /** IDLE, RUNNING, DONE, FAILED */
    @Column(name = "synthesis_status", length = 16)
    private String synthesisStatus;

    @Lob
    @Column(name = "synthesis_result_json")
    private String synthesisResultJson;

    @Column(name = "synthesis_error", length = 512)
    private String synthesisError;

    protected RetroBoardEntity() {}

    public RetroBoardEntity(String id, Instant startedAt, Instant endsAt) {
        this.id = id;
        this.startedAt = startedAt;
        this.endsAt = endsAt;
        this.synthesisStatus = "IDLE";
    }

    public String getId() {
        return id;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Instant endsAt) {
        this.endsAt = endsAt;
    }

    public String getSynthesisStatus() {
        return synthesisStatus;
    }

    public void setSynthesisStatus(String synthesisStatus) {
        this.synthesisStatus = synthesisStatus;
    }

    public String getSynthesisResultJson() {
        return synthesisResultJson;
    }

    public void setSynthesisResultJson(String synthesisResultJson) {
        this.synthesisResultJson = synthesisResultJson;
    }

    public String getSynthesisError() {
        return synthesisError;
    }

    public void setSynthesisError(String synthesisError) {
        this.synthesisError = synthesisError;
    }
}
