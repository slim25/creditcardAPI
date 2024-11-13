package com.interview.task.creditcardAPI.service.impl;

import com.interview.task.creditcardAPI.dto.LoginRequest;
import com.interview.task.creditcardAPI.exception.InvalidTokenException;
import com.interview.task.creditcardAPI.exception.RefreshTokenExpiredException;
import com.interview.task.creditcardAPI.exception.UserAlreadyExistsException;
import com.interview.task.creditcardAPI.model.TokenBlacklistEntry;
import com.interview.task.creditcardAPI.model.User;
import com.interview.task.creditcardAPI.repository.jpa.TokenBlacklistRepository;
import com.interview.task.creditcardAPI.repository.jpa.UserRepository;
import com.interview.task.creditcardAPI.security.JwtUtil;
import com.interview.task.creditcardAPI.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Autowired
    public AuthServiceImpl(JwtUtil jwtUtil, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder,
                           UserRepository userRepository, TokenBlacklistRepository tokenBlacklistRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    @Override
    public void registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("Username is already taken.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Set<String> roles = user.getRoles().isEmpty() ? Set.of("ROLE_USER") : user.getRoles();
        user.setRoles(roles);

        userRepository.save(user);
    }

    @Override
    public Map<String, String> getTokens(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        org.springframework.security.core.userdetails.User user =
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        String role = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        String accessToken = jwtUtil.generateAccessToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        tokens.put("role", role);
        return tokens;
    }

    @Override
    public Map<String, String> validateRefreshToken(String refreshToken) {
        if (jwtUtil.validateToken(refreshToken)) {
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            String newAccessToken = jwtUtil.generateAccessToken(username);
            String newRefreshToken = jwtUtil.generateRefreshToken(username);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", newAccessToken);
            tokens.put("refreshToken", newRefreshToken);

            return tokens;
        } else {
            throw new RefreshTokenExpiredException("refresh_token_expired");
        }
    }

    @Override
    public void logout(String refreshToken) {
        if (jwtUtil.validateToken(refreshToken)) {
            Instant expiresAt = jwtUtil.getExpirationFromToken(refreshToken);
            tokenBlacklistRepository.save(new TokenBlacklistEntry(refreshToken, expiresAt));
        } else {
            throw new InvalidTokenException("Invalid or expired refresh token.");
        }
    }
}
