package com.interview.task.creditcardAPI.controller;

import com.interview.task.creditcardAPI.dto.UserCreditCardsDTO;
import com.interview.task.creditcardAPI.dto.UserProfileDTO;
import com.interview.task.creditcardAPI.facade.UserProfileFacade;
import com.interview.task.creditcardAPI.model.UserProfile;
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
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserProfileControllerTest {

    @InjectMocks
    private UserProfileController userProfileController;

    @Mock
    private UserProfileFacade userProfileFacade;

    @Mock
    private UserActivityLogService activityLogService;

    @Mock
    private UserUtils userUtils;

    @Mock
    private ValidationUtils validationUtils;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateOrUpdateUserProfileSuccess() {
        UserProfile userProfile = new UserProfile();
        userProfile.setName("John Doe");

        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setName("John Doe");

        when(userProfileFacade.createOrUpdateUserProfile(any(UserDetails.class), any(UserProfile.class)))
                .thenReturn(Optional.of(profileDTO));

        ResponseEntity<UserProfileDTO> response = userProfileController.createOrUpdateUserProfile(userDetails, userProfile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(profileDTO, response.getBody());
        verify(activityLogService, times(1)).logActivity(any(), eq("CREATE_OR_UPDATE_USER_PROFILE"), anyString());
    }

    @Test
    public void testCreateOrUpdateUserProfileFailure() {
        UserProfile userProfile = new UserProfile();
        when(userProfileFacade.createOrUpdateUserProfile(any(UserDetails.class), any(UserProfile.class)))
                .thenReturn(Optional.empty());

        ResponseEntity<UserProfileDTO> response = userProfileController.createOrUpdateUserProfile(userDetails, userProfile);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(activityLogService, never()).logActivity(any(), anyString(), anyString());
    }

    @Test
    public void testGetUserProfileSuccess() {
        UserProfileDTO profileDTO = new UserProfileDTO();
        profileDTO.setName("John Doe");

        when(userProfileFacade.getUserProfile(any(UserDetails.class))).thenReturn(Optional.of(profileDTO));

        ResponseEntity<UserProfileDTO> response = userProfileController.getUserProfile(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(profileDTO, response.getBody());
        verify(activityLogService, times(1)).logActivity(any(), eq("GET_USER_PROFILE"), anyString());
    }

    @Test
    public void testGetUserProfileFailure() {
        when(userProfileFacade.getUserProfile(any(UserDetails.class))).thenReturn(Optional.empty());

        ResponseEntity<UserProfileDTO> response = userProfileController.getUserProfile(userDetails);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(activityLogService, never()).logActivity(any(), anyString(), anyString());
    }

    @Test
    public void testGetAllUserProfiles() {
        UserCreditCardsDTO userCreditCardsDTO = new UserCreditCardsDTO(new UserProfileDTO(), Collections.emptyList());
        when(userProfileFacade.getUserProfilesWithCards()).thenReturn(Collections.singletonList(userCreditCardsDTO));

        ResponseEntity<List<UserCreditCardsDTO>> response = userProfileController.getAllUserProfiles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.singletonList(userCreditCardsDTO), response.getBody());
        verify(activityLogService, times(1)).logActivity(any(), eq("GET_ALL_USER_PROFILES"), anyString());
    }
}
