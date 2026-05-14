package com.example.springrestdemo.service;

import com.example.springrestdemo.dto.TaskPatchRequest;
import com.example.springrestdemo.dto.TaskResponse;
import com.example.springrestdemo.entity.TaskEntity;
import com.example.springrestdemo.repository.TaskRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findAll() {
        return taskRepository.findAllByOrderByIdAsc().stream().map(TaskService::toResponse).toList();
    }

    @Transactional
    public TaskResponse patch(String id, TaskPatchRequest patch) {
        TaskEntity e =
                taskRepository
                        .findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        if (patch.status() != null) {
            e.setStatus(patch.status());
        }
        if (patch.qualityScore() != null) {
            e.setQualityScore(patch.qualityScore());
        }
        taskRepository.save(e);
        return toResponse(e);
    }

    private static TaskResponse toResponse(TaskEntity e) {
        return new TaskResponse(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getAssigneeId(),
                e.getStatus(),
                e.getStoryPoint(),
                e.getQualityScore());
    }
}
