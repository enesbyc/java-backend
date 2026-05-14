package com.example.springrestdemo.controller;

import com.example.springrestdemo.dto.TaskPatchRequest;
import com.example.springrestdemo.dto.TaskResponse;
import com.example.springrestdemo.service.TaskStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Task", description = "Görev listesi ve güncelleme")
public class TaskController {

    private final TaskStore taskStore;

    public TaskController(TaskStore taskStore) {
        this.taskStore = taskStore;
    }

    @GetMapping("/tasks")
    @Operation(summary = "Görev listesi")
    public List<TaskResponse> getTasks() {
        return taskStore.findAll();
    }

    @PatchMapping(path = "/tasks/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Görev kısmi güncelleme", description = "status ve/veya qualityScore alanları gönderilebilir.")
    public TaskResponse patchTask(@PathVariable String id, @RequestBody TaskPatchRequest body) {
        return taskStore.patch(id, body);
    }
}
