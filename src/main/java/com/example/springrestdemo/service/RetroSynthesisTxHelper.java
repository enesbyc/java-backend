package com.example.springrestdemo.service;

import com.example.springrestdemo.dto.RetroSynthesisSnapshot;
import com.example.springrestdemo.entity.RetroBoardEntity;
import com.example.springrestdemo.entity.RetroCardEntity;
import com.example.springrestdemo.entity.RetroVoteType;
import com.example.springrestdemo.repository.RetroBoardRepository;
import com.example.springrestdemo.repository.RetroCardRepository;
import com.example.springrestdemo.repository.RetroCardVoteRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetroSynthesisTxHelper {

    private final RetroBoardRepository boardRepository;
    private final RetroCardRepository cardRepository;
    private final RetroCardVoteRepository voteRepository;

    public RetroSynthesisTxHelper(
            RetroBoardRepository boardRepository,
            RetroCardRepository cardRepository,
            RetroCardVoteRepository voteRepository) {
        this.boardRepository = boardRepository;
        this.cardRepository = cardRepository;
        this.voteRepository = voteRepository;
    }

    @Transactional(readOnly = true)
    public RetroSynthesisSnapshot loadSnapshot(String boardId) {
        boardRepository
                .findById(boardId)
                .orElseThrow(() -> new IllegalStateException("Pano bulunamadı"));
        List<RetroCardEntity> entities = cardRepository.findByBoardIdOrderByCreatedAtAsc(boardId);
        List<String> cardIds = entities.stream().map(RetroCardEntity::getId).toList();
        Map<String, long[]> agg = voteAggregates(cardIds);
        List<RetroSynthesisSnapshot.RetroSynthesisCardInput> cards = new ArrayList<>();
        for (RetroCardEntity c : entities) {
            long[] ad = agg.getOrDefault(c.getId(), new long[] {0L, 0L});
            long net = ad[0] - ad[1];
            cards.add(new RetroSynthesisSnapshot.RetroSynthesisCardInput(
                    c.getId(), c.getContent(), ad[0], ad[1], net));
        }
        cards.sort(Comparator.comparingLong(RetroSynthesisSnapshot.RetroSynthesisCardInput::netScore).reversed());
        return new RetroSynthesisSnapshot(boardId, cards);
    }

    private Map<String, long[]> voteAggregates(List<String> cardIds) {
        Map<String, long[]> out = new HashMap<>();
        for (String cid : cardIds) {
            out.put(cid, new long[] {0L, 0L});
        }
        if (cardIds.isEmpty()) {
            return out;
        }
        for (Object[] row : voteRepository.countByCardIdsGrouped(cardIds)) {
            String cardId = (String) row[0];
            RetroVoteType t = (RetroVoteType) row[1];
            long cnt = (Long) row[2];
            long[] ad = out.computeIfAbsent(cardId, k -> new long[] {0L, 0L});
            if (t == RetroVoteType.AGREE) {
                ad[0] = cnt;
            } else {
                ad[1] = cnt;
            }
        }
        return out;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markDone(String boardId, String resultJson) {
        RetroBoardEntity b = boardRepository.findById(boardId).orElseThrow();
        b.setSynthesisStatus("DONE");
        b.setSynthesisResultJson(resultJson);
        b.setSynthesisError(null);
        boardRepository.save(b);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(String boardId, String error) {
        RetroBoardEntity b = boardRepository.findById(boardId).orElseThrow();
        b.setSynthesisStatus("FAILED");
        b.setSynthesisResultJson(null);
        String msg = error == null ? "Bilinmeyen hata" : error;
        b.setSynthesisError(msg.length() > 500 ? msg.substring(0, 500) : msg);
        boardRepository.save(b);
    }
}
