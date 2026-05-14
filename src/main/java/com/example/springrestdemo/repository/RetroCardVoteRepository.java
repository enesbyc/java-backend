package com.example.springrestdemo.repository;

import com.example.springrestdemo.entity.RetroCardVoteEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RetroCardVoteRepository extends JpaRepository<RetroCardVoteEntity, String> {

    Optional<RetroCardVoteEntity> findByCardIdAndPersonId(String cardId, String personId);

    @Query(
            "select count(v) from RetroCardVoteEntity v join RetroCardEntity c on v.cardId = c.id where c.boardId = :boardId and v.personId = :personId")
    long countByBoardAndPerson(@Param("boardId") String boardId, @Param("personId") String personId);

    @Query(
            "select v.cardId, v.voteType, count(v) from RetroCardVoteEntity v where v.cardId in :cardIds group by v.cardId, v.voteType")
    List<Object[]> countByCardIdsGrouped(@Param("cardIds") Collection<String> cardIds);

    List<RetroCardVoteEntity> findByPersonIdAndCardIdIn(String personId, Collection<String> cardIds);

    void deleteByCardIdAndPersonId(String cardId, String personId);
}
