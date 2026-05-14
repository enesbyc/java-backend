package com.example.springrestdemo.config;

import com.example.springrestdemo.repository.RetroBoardRepository;
import com.example.springrestdemo.repository.RetroCardEmojiRepository;
import com.example.springrestdemo.repository.RetroCardRepository;
import com.example.springrestdemo.repository.RetroCardVoteRepository;
import com.example.springrestdemo.repository.RetroGroupRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Her uygulama açılışında retro panosu verilerini siler (H2 veya kalıcı DB).
 * En erken çalışır; eski panolar yüzünden yanlış UI (ör. süre bitir) kalmasın.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RetroStartupReset implements CommandLineRunner {

    private final RetroCardEmojiRepository emojis;
    private final RetroCardVoteRepository votes;
    private final RetroCardRepository cards;
    private final RetroGroupRepository groups;
    private final RetroBoardRepository boards;

    public RetroStartupReset(
            RetroCardEmojiRepository emojis,
            RetroCardVoteRepository votes,
            RetroCardRepository cards,
            RetroGroupRepository groups,
            RetroBoardRepository boards) {
        this.emojis = emojis;
        this.votes = votes;
        this.cards = cards;
        this.groups = groups;
        this.boards = boards;
    }

    @Override
    @Transactional
    public void run(String... args) {
        emojis.deleteAllInBatch();
        votes.deleteAllInBatch();
        cards.deleteAllInBatch();
        groups.deleteAllInBatch();
        boards.deleteAllInBatch();
    }
}
