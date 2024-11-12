package com.interview.task.creditcardAPI.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger LOG = LoggerFactory.getLogger(JwtUtil.class);
    private final SecretKey jwtSecretKey;

    private final int accessExpirationMs;

    private final int refreshExpirationMs;
    public JwtUtil(@Value("${jwt.secret}") String jwtSecret, @Value("${jwt.accessExpirationMs}") int accessExpirationMs,
                   @Value("${jwt.refreshExpirationMs}") int refreshExpirationMs) {
        // If the provided jwt.secret is less than 64 characters, generate a secure key
        if (jwtSecret.length() < 64) {
            this.jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
            LOG.warn("Generated secure key due to short jwt.secret length.");
        } else {
            this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        }
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generateAccessToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + accessExpirationMs))
                .signWith(jwtSecretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + refreshExpirationMs))
                .signWith(jwtSecretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
    public Instant getExpirationFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(token).getBody();
        return claims.getExpiration().toInstant();
    }

}
