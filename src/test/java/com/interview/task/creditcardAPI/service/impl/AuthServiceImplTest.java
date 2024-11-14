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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenBlacklistRepository tokenBlacklistRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_ShouldThrowException_WhenUserAlreadyExists() {
        User user = new User();
        user.setUsername("existingUser");

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExistsException.class, () -> authService.registerUser(user));
    }

    @Test
    void getTokens_ShouldReturnAccessTokenAndRefreshToken_WhenLoginIsSuccessful() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("testPassword");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new org.springframework.security.core.userdetails.User(
                "testUser", "password", Set.of()));

        when(jwtUtil.generateAccessToken(loginRequest.getUsername())).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(loginRequest.getUsername())).thenReturn("refreshToken");

        Map<String, String> tokens = authService.getTokens(loginRequest);

        assertEquals("accessToken", tokens.get("accessToken"));
        assertEquals("refreshToken", tokens.get("refreshToken"));
    }

    @Test
    void validateRefreshToken_ShouldThrowException_WhenRefreshTokenIsInvalid() {
        String invalidRefreshToken = "invalidToken";

        when(jwtUtil.validateToken(invalidRefreshToken)).thenReturn(false);

        assertThrows(RefreshTokenExpiredException.class, () -> authService.validateRefreshToken(invalidRefreshToken));
    }

    @Test
    void logout_ShouldAddTokenToBlacklist_WhenTokenIsValid() {
        String validRefreshToken = "validToken";

        when(jwtUtil.validateToken(validRefreshToken)).thenReturn(true);
        when(jwtUtil.getExpirationFromToken(validRefreshToken)).thenReturn(Instant.now().plusSeconds(3600));

        authService.logout(validRefreshToken);

        verify(tokenBlacklistRepository, times(1)).save(any(TokenBlacklistEntry.class));
    }

    @Test
    void logout_ShouldThrowInvalidTokenException_WhenTokenIsInvalid() {
        String invalidToken = "invalidToken";

        when(jwtUtil.validateToken(invalidToken)).thenReturn(false);

        assertThrows(InvalidTokenException.class, () -> authService.logout(invalidToken));
    }
}
