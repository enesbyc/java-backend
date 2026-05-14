package com.example.springrestdemo.repository;

import com.example.springrestdemo.entity.RetroCardEmojiEntity;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RetroCardEmojiRepository extends JpaRepository<RetroCardEmojiEntity, String> {

    @Query(
            "select e.cardId, e.emoji, count(e) from RetroCardEmojiEntity e where e.cardId in :cardIds group by e.cardId, e.emoji")
    List<Object[]> countByCardIdsGrouped(@Param("cardIds") Collection<String> cardIds);
}
