package com.example.springrestdemo.repository;

import com.example.springrestdemo.entity.EntryBoardItemEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntryBoardItemRepository extends JpaRepository<EntryBoardItemEntity, String> {

    List<EntryBoardItemEntity> findByBoardIdOrderByCreatedAtAsc(String boardId);
}
