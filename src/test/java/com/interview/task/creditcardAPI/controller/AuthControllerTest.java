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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @Mock
    private UserActivityLogService activityLogService;

    @Mock
    private UserUtils userUtils;

    @Mock
    private ValidationUtils validationUtils;
    @Mock
    private TokenBlacklistRepository tokenBlacklistRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterUserSuccess() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");

        ResponseEntity<String> response = authController.register(user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully.", response.getBody());
        verify(activityLogService, times(1)).logActivity(any(), eq("REGISTRATION"), anyString());
    }

    @Test
    public void testRegisterUserDuplicateUsername() {
        User user = new User();
        user.setUsername("existinguser");

        doThrow(new UserAlreadyExistsException("Username is already taken."))
                .when(authService).registerUser(any(User.class));

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> authController.register(user)
        );

        assertEquals("Username is already taken.", exception.getMessage());
    }

    @Test
    public void testLoginSuccess() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        Map<String, String> tokens = Map.of("accessToken", "mockAccessToken", "refreshToken", "mockRefreshToken");
        when(authService.getTokens(any())).thenReturn(tokens);

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(activityLogService, times(1)).logActivity(any(), eq("LOGIN"), anyString());
    }

    @Test
    public void testLoginFailure() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("wronguser");
        loginRequest.setPassword("wrongpassword");

        when(authService.getTokens(any())).thenThrow(new RuntimeException("Invalid credentials"));

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testRefreshTokenSuccess() {
        String validRefreshToken = "validToken";
        Map<String, String> newTokens = Map.of("accessToken", "newAccessToken", "refreshToken", "newRefreshToken");

        when(authService.validateRefreshToken(validRefreshToken)).thenReturn(newTokens);

        ResponseEntity<Map<String, String>> response = authController.refreshAccessToken(Map.of("refreshToken", validRefreshToken));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(newTokens, response.getBody());
    }


    @Test
    public void testLogoutSuccess() {
        String refreshToken = "validToken";
        ResponseEntity<String> response = authController.logout(Map.of("refreshToken", refreshToken));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(activityLogService, times(1)).logActivity(any(), eq("LOGOUT"), anyString());
    }
}
