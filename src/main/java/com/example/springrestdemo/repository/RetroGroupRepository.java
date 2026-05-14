package com.example.springrestdemo.repository;

import com.example.springrestdemo.entity.RetroGroupEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetroGroupRepository extends JpaRepository<RetroGroupEntity, String> {

    List<RetroGroupEntity> findByBoardIdOrderBySortOrderAsc(String boardId);
}
