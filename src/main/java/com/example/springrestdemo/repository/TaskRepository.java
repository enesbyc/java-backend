package com.example.springrestdemo.repository;

import com.example.springrestdemo.entity.TaskEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskEntity, String> {

    List<TaskEntity> findAllByOrderByIdAsc();
}
