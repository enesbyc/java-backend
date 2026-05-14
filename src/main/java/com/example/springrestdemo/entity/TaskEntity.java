package com.example.springrestdemo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tasks")
public class TaskEntity {

    @Id
    @Column(length = 64)
    private String id;

    @Column(nullable = false, length = 512)
    private String title;

    @Column(nullable = false, length = 2048)
    private String description;

    @Column(name = "assignee_id", nullable = false, length = 64)
    private String assigneeId;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(name = "story_point", nullable = false)
    private int storyPoint;

    @Column(name = "quality_score")
    private Integer qualityScore;

    protected TaskEntity() {}

    public TaskEntity(
            String id,
            String title,
            String description,
            String assigneeId,
            String status,
            int storyPoint,
            Integer qualityScore) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.assigneeId = assigneeId;
        this.status = status;
        this.storyPoint = storyPoint;
        this.qualityScore = qualityScore;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public String getStatus() {
        return status;
    }

    public int getStoryPoint() {
        return storyPoint;
    }

    public Integer getQualityScore() {
        return qualityScore;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setQualityScore(Integer qualityScore) {
        this.qualityScore = qualityScore;
    }
}
