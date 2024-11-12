package com.interview.task.creditcardAPI.service.impl;

import com.interview.task.creditcardAPI.repository.jpa.TokenBlacklistRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenCleanupServiceImpl {

    private final TokenBlacklistRepository tokenBlacklistRepository;

    public TokenCleanupServiceImpl(TokenBlacklistRepository tokenBlacklistRepository) {
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    @Scheduled(fixedRate = 86400000) // every 24 hours
    public void cleanupExpiredTokens() {
        tokenBlacklistRepository.deleteByExpiresAtBefore(Instant.now());
    }
}
