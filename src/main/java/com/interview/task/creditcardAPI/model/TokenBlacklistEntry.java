package com.interview.task.creditcardAPI.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenBlacklistEntry {
    @Id
    private String token;
    private Instant expiresAt;

}
