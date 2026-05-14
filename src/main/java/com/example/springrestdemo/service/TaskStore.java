package com.example.springrestdemo.service;

import com.example.springrestdemo.dto.TaskPatchRequest;
import com.example.springrestdemo.dto.TaskResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TaskStore {

    private static final class Entity {
        String id;
        String title;
        String description;
        String assigneeId;
        String status;
        int storyPoint;
        Integer qualityScore;
    }

    private final List<Entity> tasks = new ArrayList<>();

    public TaskStore() {
        seed();
    }

    private void seed() {
        add("t1", "Login Sayfası Tasarımı", "Kullanıcı giriş ekranının UI tasarımı", "p1", "done", 3, 4);
        add("t2", "API Entegrasyonu", "Backend servislerine bağlantı kurulması", "p1", "in-progress", 5, null);
        add("t3", "Dashboard Grafikleri", "Yönetim paneli grafik bileşenleri", "p2", "todo", 8, null);
        add("t4", "Bildirim Sistemi", "Push notification altyapısı", "p2", "done", 5, 5);
        add("t5", "Veritabanı Optimizasyonu", "Sorgu performans iyileştirmesi", "p3", "in-progress", 8, null);
        add("t6", "Unit Test Yazımı", "Kritik modüller için test coverage", "p3", "todo", 3, null);
        add("t7", "Responsive Tasarım", "Mobil uyumluluk düzenlemeleri", "p1", "done", 2, 3);
        add("t8", "CI/CD Pipeline", "Otomatik deploy sürecinin kurulması", "p2", "in-progress", 5, null);
    }

    private void add(
            String id,
            String title,
            String description,
            String assigneeId,
            String status,
            int storyPoint,
            Integer qualityScore) {
        Entity e = new Entity();
        e.id = id;
        e.title = title;
        e.description = description;
        e.assigneeId = assigneeId;
        e.status = status;
        e.storyPoint = storyPoint;
        e.qualityScore = qualityScore;
        tasks.add(e);
    }

    public List<TaskResponse> findAll() {
        return tasks.stream().map(TaskStore::toResponse).toList();
    }

    public TaskResponse patch(String id, TaskPatchRequest patch) {
        Entity e =
                tasks.stream()
                        .filter(t -> t.id.equals(id))
                        .findFirst()
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (patch.status() != null) {
            e.status = patch.status();
        }
        if (patch.qualityScore() != null) {
            e.qualityScore = patch.qualityScore();
        }
        return toResponse(e);
    }

    private static TaskResponse toResponse(Entity e) {
        return new TaskResponse(e.id, e.title, e.description, e.assigneeId, e.status, e.storyPoint, e.qualityScore);
    }
}
