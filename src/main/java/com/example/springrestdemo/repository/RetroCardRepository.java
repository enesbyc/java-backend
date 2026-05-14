package com.example.springrestdemo.repository;

import com.example.springrestdemo.entity.RetroCardEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetroCardRepository extends JpaRepository<RetroCardEntity, String> {

    List<RetroCardEntity> findByBoardIdOrderByCreatedAtAsc(String boardId);
}
