package com.interview.task.creditcardAPI.utils;

import com.interview.task.creditcardAPI.model.User;
import com.interview.task.creditcardAPI.repository.jpa.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserUtilsTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserUtils userUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUsername_ShouldReturnUsername_WhenUserIsAuthenticated() {
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testUser");

        try {
            String username = userUtils.getCurrentUsername();
            assertEquals("testUser", username);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void getCurrentUsername_ShouldThrowException_WhenUserIsNotAuthenticated() {
        SecurityContextHolder.clearContext();
        assertThrows(UsernameNotFoundException.class, userUtils::getCurrentUsername);
    }

    @Test
    void getCurrentUserId_ShouldReturnUserId_WhenUserExists() {
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(authentication.getName()).thenReturn("testUser");
        when(authentication.isAuthenticated()).thenReturn(true);

        User mockUser = new User();
        mockUser.setId(1L);
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));

        try {
            Long userId = userUtils.getCurrentUserId();
            assertEquals(1L, userId);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void getCurrentUserId_ShouldThrowException_WhenUserDoesNotExist() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("nonExistentUser", "password");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, userUtils::getCurrentUserId);
    }

    @Test
    void getCurrentUserId_ShouldThrowException_WhenUsernameIsBlank() {
        assertThrows(UsernameNotFoundException.class, () -> userUtils.getCurrentUserId(""));
    }

    @Test
    void getCurrentUserId_ShouldReturnUserId_WhenUsernameIsProvided() {
        User mockUser = new User();
        mockUser.setId(2L);
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(mockUser));

        Long userId = userUtils.getCurrentUserId("testUser");
        assertEquals(2L, userId);
    }

    @Test
    void getCurrentUserId_ShouldThrowException_WhenUsernameIsNotFound() {
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userUtils.getCurrentUserId("unknownUser"));
    }
}
