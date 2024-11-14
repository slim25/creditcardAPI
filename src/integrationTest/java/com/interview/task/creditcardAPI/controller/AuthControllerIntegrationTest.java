package com.interview.task.creditcardAPI.controller;

import com.interview.task.creditcardAPI.dto.LoginRequest;
import com.interview.task.creditcardAPI.exception.UserAlreadyExistsException;
import com.interview.task.creditcardAPI.model.User;
import com.interview.task.creditcardAPI.repository.jpa.TokenBlacklistRepository;
import com.interview.task.creditcardAPI.service.AuthService;
import com.interview.task.creditcardAPI.service.UserActivityLogService;
import com.interview.task.creditcardAPI.utils.UserUtils;
import com.interview.task.creditcardAPI.utils.ValidationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private TokenBlacklistRepository tokenBlacklistRepository;

    @MockBean
    private UserActivityLogService activityLogService;

    @MockBean
    private UserUtils userUtils;

    @MockBean
    private ValidationUtils validationUtils;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        doNothing().when(authService).registerUser(any(User.class));
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testuser\", \"password\": \"password\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully."));
    }

    @Test
    void shouldFailRegister_WhenUserAlreadyExists() throws Exception {
        doThrow(new UserAlreadyExistsException("Username is already taken.")).when(authService).registerUser(any(User.class));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"existinguser\", \"password\": \"password\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username is already taken."));
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");
        when(authService.getTokens(any())).thenReturn(Map.of("accessToken", "mockAccessToken", "refreshToken", "mockRefreshToken"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testuser\", \"password\": \"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("mockAccessToken"))
                .andExpect(jsonPath("$.refreshToken").value("mockRefreshToken"));
    }

    @Test
    void shouldFailLogin_WithInvalidCredentials() throws Exception {
        when(authService.getTokens(any())).thenThrow(new RuntimeException("Invalid credentials"));
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"invaliduser\", \"password\": \"wrongpass\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("{\"error\":\"Invalid credentials\"}"));
    }

    @Test
    void shouldRefreshTokenSuccessfully() throws Exception {
        String validRefreshToken = "validToken";
        when(tokenBlacklistRepository.existsByToken(validRefreshToken)).thenReturn(false);
        when(authService.validateRefreshToken(validRefreshToken)).thenReturn(Map.of("accessToken", "newAccessToken", "refreshToken", "newRefreshToken"));

        mockMvc.perform(post("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\": \"validToken\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newAccessToken"))
                .andExpect(jsonPath("$.refreshToken").value("newRefreshToken"));
    }

    @Test
    void shouldFailRefreshToken_WhenTokenIsExpired() throws Exception {
        when(tokenBlacklistRepository.existsByToken("expiredToken")).thenReturn(true);
        mockMvc.perform(post("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\": \"expiredToken\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("{\"error\":\"Invalid or expired refresh token\"}"));
    }

    @Test
    void shouldLogoutSuccessfully() throws Exception {
        String refreshToken = "validToken";
        doNothing().when(authService).logout(refreshToken);
        when(userUtils.getCurrentUserId()).thenReturn(1L);
        doNothing().when(activityLogService).logActivity(anyLong(), anyString(), anyString());

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\": \"validToken\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out successfully."));
    }
}