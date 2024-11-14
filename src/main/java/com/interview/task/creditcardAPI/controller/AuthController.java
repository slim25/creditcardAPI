package com.interview.task.creditcardAPI.controller;

import com.interview.task.creditcardAPI.dto.LoginRequest;
import com.interview.task.creditcardAPI.model.User;
import com.interview.task.creditcardAPI.repository.jpa.TokenBlacklistRepository;
import com.interview.task.creditcardAPI.service.AuthService;
import com.interview.task.creditcardAPI.service.UserActivityLogService;
import com.interview.task.creditcardAPI.utils.UserUtils;
import com.interview.task.creditcardAPI.utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final UserActivityLogService activityLogService;
    private final UserUtils userUtils;
    private final AuthService authService;
    private final ValidationUtils validationUtils;

    @Autowired
    public AuthController(TokenBlacklistRepository tokenBlacklistRepository, UserActivityLogService activityLogService,
                          UserUtils userUtils, AuthService authService, ValidationUtils validationUtils) {
        this.tokenBlacklistRepository = tokenBlacklistRepository;
        this.activityLogService = activityLogService;
        this.userUtils = userUtils;
        this.authService = authService;
        this.validationUtils = validationUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) { // substitute user with DTO
        validationUtils.validateUserRegistration(user);
        authService.registerUser(user);

        LOG.debug("User {} registered in successfully with role {}", user.getUsername(), user.getRoles());
        activityLogService.logActivity(user.getId(), "REGISTRATION", "User registered with username: " + user.getUsername());

        return ResponseEntity.ok("User registered successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        validationUtils.validateUserLogin(loginRequest);
        String username = null;
        try {
            username = loginRequest.getUsername();
            Map<String, String> tokens = authService.getTokens(loginRequest);

            Long userId = userUtils.getCurrentUserId(username);
            LOG.debug("User {} logged in successfully", username);
            activityLogService.logActivity(userId, "LOGIN", "User logged in with username: " + username);

            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            LOG.warn("Failed login attempt for user {}", username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (tokenBlacklistRepository.existsByToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired refresh token"));
        }

        Map<String, String> tokens = authService.validateRefreshToken(refreshToken);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        authService.logout(refreshToken);
        Long userId = userUtils.getCurrentUserId();
        LOG.debug("User with id {} logout successfully", userId);
        activityLogService.logActivity(userId, "LOGOUT", "User logout successfully");

        return ResponseEntity.ok("Logged out successfully.");

    }
}
