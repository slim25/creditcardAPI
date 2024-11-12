package com.interview.task.creditcardAPI.repository.jpa;

import com.interview.task.creditcardAPI.model.TokenBlacklistEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklistEntry, String> {
    boolean existsByToken(String token);
    void deleteByExpiresAtBefore(Instant now);
}
