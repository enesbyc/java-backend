package com.example.springrestdemo.service;

import com.example.springrestdemo.dto.AddRetroCardEmojiRequest;
import com.example.springrestdemo.dto.CreateRetroBoardRequest;
import com.example.springrestdemo.dto.CreateRetroCardRequest;
import com.example.springrestdemo.dto.PatchRetroCardRequest;
import com.example.springrestdemo.dto.RetroBoardCreatedResponse;
import com.example.springrestdemo.dto.RetroBoardStateResponse;
import com.example.springrestdemo.dto.RetroCardDto;
import com.example.springrestdemo.dto.RetroGroupDto;
import com.example.springrestdemo.dto.SetRetroCardVoteRequest;
import com.example.springrestdemo.entity.PersonEntity;
import com.example.springrestdemo.entity.PersonRole;
import com.example.springrestdemo.entity.RetroBoardEntity;
import com.example.springrestdemo.entity.RetroCardEmojiEntity;
import com.example.springrestdemo.entity.RetroCardEntity;
import com.example.springrestdemo.entity.RetroCardVoteEntity;
import com.example.springrestdemo.entity.RetroGroupEntity;
import com.example.springrestdemo.entity.RetroVoteType;
import com.example.springrestdemo.repository.PersonRepository;
import com.example.springrestdemo.repository.RetroBoardRepository;
import com.example.springrestdemo.repository.RetroCardEmojiRepository;
import com.example.springrestdemo.repository.RetroCardRepository;
import com.example.springrestdemo.repository.RetroCardVoteRepository;
import com.example.springrestdemo.repository.RetroGroupRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RetroBoardService {

    private static final int MAX_VOTES_PER_PERSON = 3;

    private final RetroBoardRepository boardRepository;
    private final RetroGroupRepository groupRepository;
    private final RetroCardRepository cardRepository;
    private final RetroCardEmojiRepository emojiRepository;
    private final RetroCardVoteRepository voteRepository;
    private final PersonRepository personRepository;
    private final RetroAiSynthesisJob retroAiSynthesisJob;
    private final ObjectMapper objectMapper;

    public RetroBoardService(
            RetroBoardRepository boardRepository,
            RetroGroupRepository groupRepository,
            RetroCardRepository cardRepository,
            RetroCardEmojiRepository emojiRepository,
            RetroCardVoteRepository voteRepository,
            PersonRepository personRepository,
            RetroAiSynthesisJob retroAiSynthesisJob,
            ObjectMapper objectMapper) {
        this.boardRepository = boardRepository;
        this.groupRepository = groupRepository;
        this.cardRepository = cardRepository;
        this.emojiRepository = emojiRepository;
        this.voteRepository = voteRepository;
        this.personRepository = personRepository;
        this.retroAiSynthesisJob = retroAiSynthesisJob;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public RetroBoardCreatedResponse createBoard(CreateRetroBoardRequest req) {
        int minutes = req.durationMinutes();
        String boardId = "rb_" + UUID.randomUUID().toString().replace("-", "");
        Instant now = Instant.now();
        Instant ends = now.plusSeconds(minutes * 60L);
        boardRepository.save(new RetroBoardEntity(boardId, now, ends));

        String g1 = boardId + "_g1";
        String g2 = boardId + "_g2";
        groupRepository.save(new RetroGroupEntity(g1, boardId, "İyi gidenler", 0));
        groupRepository.save(new RetroGroupEntity(g2, boardId, "Geliştirilicek alanlar", 1));

        List<RetroGroupDto> groups =
                List.of(new RetroGroupDto(g1, "İyi gidenler"), new RetroGroupDto(g2, "Geliştirilicek alanlar"));
        return new RetroBoardCreatedResponse(boardId, ends.toString(), groups);
    }

    @Transactional(readOnly = true)
    public RetroBoardStateResponse getBoard(String boardId, String viewerId) {
        RetroBoardEntity board = boardRepository
                .findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found"));
        Instant now = Instant.now();
        boolean revealed = !now.isBefore(board.getEndsAt());
        String viewer = viewerId == null ? "" : viewerId.trim();

        List<RetroGroupDto> groups = groupRepository.findByBoardIdOrderBySortOrderAsc(boardId).stream()
                .map(g -> new RetroGroupDto(g.getId(), g.getTitle()))
                .toList();

        List<RetroCardEntity> cardEntities = cardRepository.findByBoardIdOrderByCreatedAtAsc(boardId);
        List<String> cardIds = cardEntities.stream().map(RetroCardEntity::getId).toList();

        Map<String, Map<String, Long>> emojiByCard = loadEmojiCounts(cardIds);
        Map<String, long[]> voteAgg = loadVoteAggregates(cardIds);
        Map<String, RetroVoteType> myVoteByCard = loadMyVotes(viewer, cardIds);

        List<RetroCardDto> cards = cardEntities.stream()
                .map(c -> buildCardDto(c, revealed, viewer, emojiByCard, voteAgg, myVoteByCard))
                .toList();

        int viewerVotesUsed = 0;
        if (!viewer.isBlank()) {
            viewerVotesUsed = (int) voteRepository.countByBoardAndPerson(boardId, viewer);
        }

        String synthStatus = board.getSynthesisStatus();
        if (synthStatus == null || synthStatus.isBlank()) {
            synthStatus = "IDLE";
        }
        JsonNode synthResult = null;
        if (board.getSynthesisResultJson() != null && !board.getSynthesisResultJson().isBlank()) {
            try {
                synthResult = objectMapper.readTree(board.getSynthesisResultJson());
            } catch (Exception ignored) {
                // yanıt bozuksa null bırak
            }
        }

        return new RetroBoardStateResponse(
                board.getId(),
                board.getStartedAt().toString(),
                board.getEndsAt().toString(),
                revealed,
                viewerVotesUsed,
                synthStatus,
                synthResult,
                board.getSynthesisError(),
                groups,
                cards);
    }

    private Map<String, Map<String, Long>> loadEmojiCounts(List<String> cardIds) {
        Map<String, Map<String, Long>> out = new HashMap<>();
        if (cardIds.isEmpty()) {
            return out;
        }
        for (Object[] row : emojiRepository.countByCardIdsGrouped(cardIds)) {
            String cardId = (String) row[0];
            String emoji = (String) row[1];
            long cnt = (Long) row[2];
            out.computeIfAbsent(cardId, k -> new LinkedHashMap<>()).merge(emoji, cnt, Long::sum);
        }
        return out;
    }

    private Map<String, long[]> loadVoteAggregates(List<String> cardIds) {
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

    private Map<String, RetroVoteType> loadMyVotes(String viewer, List<String> cardIds) {
        if (viewer.isBlank() || cardIds.isEmpty()) {
            return Map.of();
        }
        Map<String, RetroVoteType> out = new HashMap<>();
        for (RetroCardVoteEntity v : voteRepository.findByPersonIdAndCardIdIn(viewer, cardIds)) {
            out.put(v.getCardId(), v.getVoteType());
        }
        return out;
    }

    private RetroCardDto buildCardDto(
            RetroCardEntity c,
            boolean revealed,
            String viewerId,
            Map<String, Map<String, Long>> emojiByCard,
            Map<String, long[]> voteAgg,
            Map<String, RetroVoteType> myVoteByCard) {
        boolean masked = !revealed && !viewerId.equals(c.getAuthorId());
        String content = masked ? "" : c.getContent();
        Map<String, Long> emojis = emojiByCard.getOrDefault(c.getId(), Map.of());
        long[] ad = voteAgg.getOrDefault(c.getId(), new long[] {0L, 0L});
        RetroVoteType my = myVoteByCard.get(c.getId());
        String myS = my == null ? null : my.name();
        return new RetroCardDto(
                c.getId(),
                c.getGroupId(),
                c.getAuthorId(),
                content,
                masked,
                c.getCreatedAt().toString(),
                c.isShowAuthorName(),
                Collections.unmodifiableMap(new LinkedHashMap<>(emojis)),
                ad[0],
                ad[1],
                myS);
    }

    /** Tek kart yanıtı (POST/PATCH sonrası). */
    private RetroCardDto buildCardDto(RetroCardEntity c, boolean revealed, String viewerId) {
        List<String> ids = List.of(c.getId());
        Map<String, Map<String, Long>> em = loadEmojiCounts(ids);
        Map<String, long[]> va = loadVoteAggregates(ids);
        Map<String, RetroVoteType> my = loadMyVotes(viewerId, ids);
        return buildCardDto(c, revealed, viewerId, em, va, my);
    }

    private void requireBoardRevealedForEmojiVotes(String boardId) {
        RetroBoardEntity board = boardRepository
                .findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found"));
        if (Instant.now().isBefore(board.getEndsAt())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Kartlar henüz herkese açılmadı; emoji ve oy kullanılamaz");
        }
        requireSynthesisAllowsInteraction(board);
    }

    private static void requireSynthesisAllowsInteraction(RetroBoardEntity board) {
        String st = board.getSynthesisStatus();
        if (st == null || st.isBlank() || "IDLE".equalsIgnoreCase(st) || "FAILED".equalsIgnoreCase(st)) {
            return;
        }
        if ("RUNNING".equalsIgnoreCase(st) || "DONE".equalsIgnoreCase(st)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Bu adımda emoji, oy veya kart taşıma kullanılamaz");
        }
    }

    private RetroCardEntity requireCardOnBoard(String boardId, String cardId) {
        RetroCardEntity card = cardRepository
                .findById(cardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kart bulunamadı"));
        if (!boardId.equals(card.getBoardId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kart bu panoda değil");
        }
        return card;
    }

    @Transactional
    public RetroCardDto addEmoji(String boardId, String cardId, AddRetroCardEmojiRequest req) {
        requireBoardRevealedForEmojiVotes(boardId);
        String pid = req.personId().trim();
        personRepository
                .findById(pid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı"));
        RetroCardEntity card = requireCardOnBoard(boardId, cardId);
        String raw = req.emoji().strip();
        if (raw.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Emoji gerekli");
        }
        int cps = raw.codePointCount(0, raw.length());
        if (cps < 1 || cps > 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz emoji");
        }
        String id = "rce_" + UUID.randomUUID().toString().replace("-", "");
        emojiRepository.save(new RetroCardEmojiEntity(id, cardId, pid, raw, Instant.now()));
        return buildCardDto(card, true, pid);
    }

    @Transactional
    public RetroCardDto setVote(String boardId, String cardId, SetRetroCardVoteRequest req) {
        requireBoardRevealedForEmojiVotes(boardId);
        String pid = req.personId().trim();
        personRepository
                .findById(pid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı"));
        RetroCardEntity card = requireCardOnBoard(boardId, cardId);
        Optional<RetroCardVoteEntity> existing = voteRepository.findByCardIdAndPersonId(cardId, pid);
        if (existing.isPresent()) {
            RetroCardVoteEntity v = existing.get();
            v.setVoteType(req.voteType());
            voteRepository.save(v);
        } else {
            long used = voteRepository.countByBoardAndPerson(boardId, pid);
            if (used >= MAX_VOTES_PER_PERSON) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "En fazla 3 oy verebilirsiniz");
            }
            String id = "rcv_" + UUID.randomUUID().toString().replace("-", "");
            voteRepository.save(new RetroCardVoteEntity(id, cardId, pid, req.voteType(), Instant.now()));
        }
        return buildCardDto(card, true, pid);
    }

    @Transactional
    public RetroCardDto removeVote(String boardId, String cardId, String personId) {
        requireBoardRevealedForEmojiVotes(boardId);
        RetroCardEntity card = requireCardOnBoard(boardId, cardId);
        String pid = personId == null ? "" : personId.trim();
        if (pid.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "personId gerekli");
        }
        personRepository
                .findById(pid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı"));
        voteRepository.deleteByCardIdAndPersonId(cardId, pid);
        return buildCardDto(card, true, pid);
    }

    @Transactional
    public RetroCardDto addCard(String boardId, CreateRetroCardRequest req) {
        RetroBoardEntity board = boardRepository
                .findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found"));
        Instant now = Instant.now();
        if (!now.isBefore(board.getEndsAt())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Retro süresi bitti; kart eklenemez");
        }
        String gid = req.groupId();
        if (gid != null && !gid.isBlank()) {
            RetroGroupEntity g = groupRepository
                    .findById(gid.trim())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz grup"));
            if (!boardId.equals(g.getBoardId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grup bu panoya ait değil");
            }
        } else {
            gid = null;
        }

        String id = "rc_" + UUID.randomUUID().toString().replace("-", "");
        boolean showName = Boolean.TRUE.equals(req.showAuthorName());
        RetroCardEntity entity = new RetroCardEntity(
                id,
                boardId,
                gid == null || gid.isBlank() ? null : gid.trim(),
                req.content().trim(),
                req.authorId().trim(),
                showName,
                now);
        cardRepository.save(entity);
        boolean revealed = !now.isBefore(board.getEndsAt());
        return buildCardDto(entity, revealed, req.authorId().trim());
    }

    @Transactional
    public RetroCardDto patchCard(String boardId, String cardId, PatchRetroCardRequest req, String viewerId) {
        RetroCardEntity card = cardRepository
                .findById(cardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kart bulunamadı"));
        if (!boardId.equals(card.getBoardId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kart bu panoda değil");
        }
        RetroBoardEntity board = boardRepository.findById(boardId).orElseThrow();
        boolean revealed = !Instant.now().isBefore(board.getEndsAt());
        if (revealed) {
            requireSynthesisAllowsInteraction(board);
        }
        String gid = req.groupId();
        if (gid != null && !gid.isBlank()) {
            RetroGroupEntity g = groupRepository
                    .findById(gid.trim())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz grup"));
            if (!boardId.equals(g.getBoardId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grup bu panoya ait değil");
            }
            card.setGroupId(gid.trim());
        } else {
            card.setGroupId(null);
        }
        cardRepository.save(card);
        String v = viewerId == null ? "" : viewerId.trim();
        return buildCardDto(card, revealed, v);
    }

    /**
     * Lead süreyi beklemeden veri girişini bitirir; endsAt şu ana çekilir, kartlar herkese açılır.
     */
    @Transactional
    public RetroBoardStateResponse endCollectingEarly(String boardId, String actorId) {
        if (actorId == null || actorId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "actorId gerekli");
        }
        String aid = actorId.trim();
        PersonEntity actor = personRepository
                .findById(aid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı"));
        if (actor.getRole() != PersonRole.lead) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Yalnızca lead bu adımı bitirebilir");
        }

        RetroBoardEntity board = boardRepository
                .findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found"));

        Instant now = Instant.now();
        if (!now.isBefore(board.getEndsAt())) {
            return getBoard(boardId, aid);
        }
        board.setEndsAt(now);
        boardRepository.save(board);
        return getBoard(boardId, aid);
    }

    /**
     * Lead: emoji/oy adımından sonra Gemini ile gruplama ve oya göre sıralama başlatır.
     */
    @Transactional
    public RetroBoardStateResponse requestAiSynthesis(String boardId, String actorId) {
        if (actorId == null || actorId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "actorId gerekli");
        }
        String aid = actorId.trim();
        PersonEntity actor = personRepository
                .findById(aid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kullanıcı bulunamadı"));
        if (actor.getRole() != PersonRole.lead) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Yalnızca lead bu adımı başlatabilir");
        }
        RetroBoardEntity board = boardRepository
                .findById(boardId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Board not found"));
        if (Instant.now().isBefore(board.getEndsAt())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Önce veri toplama süresinin bitmesi veya lead tarafından bitirilmesi gerekir");
        }
        String st = board.getSynthesisStatus();
        if (st == null || st.isBlank()) {
            st = "IDLE";
        }
        if ("RUNNING".equalsIgnoreCase(st)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "AI işlemi zaten sürüyor");
        }
        if ("DONE".equalsIgnoreCase(st)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Özet zaten oluşturuldu");
        }
        board.setSynthesisStatus("RUNNING");
        board.setSynthesisResultJson(null);
        board.setSynthesisError(null);
        boardRepository.save(board);
        retroAiSynthesisJob.runAsync(boardId);
        return getBoard(boardId, aid);
    }
}
