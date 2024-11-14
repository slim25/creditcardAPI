package com.interview.task.creditcardAPI.service.impl;

import com.interview.task.creditcardAPI.dto.UserProfileDTO;
import com.interview.task.creditcardAPI.model.User;
import com.interview.task.creditcardAPI.model.UserProfile;
import com.interview.task.creditcardAPI.repository.jpa.UserProfileRepository;
import com.interview.task.creditcardAPI.repository.jpa.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfileServiceImplTest {

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserDetails userDetails;

    @Mock
    private CacheManager cacheManager;

    private User testUser;
    private UserProfile userProfile;
    private UserProfileDTO userProfileDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        userProfile = new UserProfile();
        userProfile.setId(1L);
        userProfile.setName("Test Name");
        userProfile.setEmail("test@example.com");
        userProfile.setPhone("123-456-7890");
        userProfile.setUser(testUser);

        userProfileDTO = new UserProfileDTO();
        userProfileDTO.setId(1L);
        userProfileDTO.setName("Test Name");
        userProfileDTO.setEmail("test@example.com");
        userProfileDTO.setPhone("123-456-7890");
        userProfileDTO.setUserId(1L);

        when(userDetails.getUsername()).thenReturn("testuser");
    }

    @Test
    void getAllUserProfiles() {
        when(userProfileRepository.findAll()).thenReturn(List.of(userProfile));

        List<UserProfileDTO> result = userProfileService.getAllUserProfiles();

        assertEquals(1, result.size());
        assertEquals("Test Name", result.get(0).getName());
        verify(userProfileRepository, times(1)).findAll();
    }

    @Test
    void createOrUpdateUserProfile_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(userProfile));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);

        Optional<UserProfileDTO> result = userProfileService.createOrUpdateUserProfile(userDetails, userProfile);

        assertTrue(result.isPresent());
        assertEquals("Test Name", result.get().getName());
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }

    @Test
    void createOrUpdateUserProfile_NewProfile() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);

        Optional<UserProfileDTO> result = userProfileService.createOrUpdateUserProfile(userDetails, userProfile);

        assertTrue(result.isPresent());
        assertEquals("Test Name", result.get().getName());
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }
    @Test
    void createOrUpdateUserProfile_UserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        Optional<UserProfileDTO> result = userProfileService.createOrUpdateUserProfile(userDetails, userProfile);

        assertFalse(result.isPresent());
        verify(userProfileRepository, never()).save(any(UserProfile.class));
    }

    @Test
    void getUserProfile_FromCache() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(userProfile));

        Optional<UserProfileDTO> result = userProfileService.getUserProfile(userDetails);

        assertTrue(result.isPresent());
        assertEquals("Test Name", result.get().getName());
        verify(userProfileRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getUserProfile_NotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.empty());

        Optional<UserProfileDTO> result = userProfileService.getUserProfile(userDetails);

        assertFalse(result.isPresent());
        verify(userProfileRepository, times(1)).findByUserId(1L);
    }
}
