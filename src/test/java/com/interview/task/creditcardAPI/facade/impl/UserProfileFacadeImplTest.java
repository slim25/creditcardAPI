package com.interview.task.creditcardAPI.facade.impl;

import com.interview.task.creditcardAPI.dto.CreditCardDTO;
import com.interview.task.creditcardAPI.dto.UserCreditCardsDTO;
import com.interview.task.creditcardAPI.dto.UserProfileDTO;
import com.interview.task.creditcardAPI.model.UserProfile;
import com.interview.task.creditcardAPI.service.CreditCardService;
import com.interview.task.creditcardAPI.service.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class UserProfileFacadeImplTest {

    @InjectMocks
    private UserProfileFacadeImpl userProfileFacade;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private CreditCardService creditCardService;

    private UserProfileDTO userProfileDTO;
    private CreditCardDTO creditCardDTO;
    private UserProfile userProfile;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Sample data for testing
        userProfileDTO = new UserProfileDTO();
        userProfileDTO.setId(1L);
        userProfileDTO.setName("Test User");
        userProfileDTO.setEmail("testuser@example.com");
        userProfileDTO.setPhone("1234567890");
        userProfileDTO.setUserId(1L);

        creditCardDTO = new CreditCardDTO("**** **** **** 1234", "Test User", "12/24", "token123");

        userProfile = new UserProfile();
        userProfile.setId(1L);
        userProfile.setName("Test User");
        userProfile.setEmail("testuser@example.com");
        userProfile.setPhone("1234567890");
    }

    @Test
    public void createOrUpdateUserProfile_Success() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        when(userProfileService.createOrUpdateUserProfile(userDetails, userProfile)).thenReturn(Optional.of(userProfileDTO));

        Optional<UserProfileDTO> result = userProfileFacade.createOrUpdateUserProfile(userDetails, userProfile);

        assertEquals(userProfileDTO, result.orElse(null));
        verify(userProfileService, times(1)).createOrUpdateUserProfile(userDetails, userProfile);
    }


    @Test
    public void createOrUpdateUserProfile_Failure() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        when(userProfileService.createOrUpdateUserProfile(userDetails, userProfile)).thenReturn(Optional.empty());

        Optional<UserProfileDTO> result = userProfileFacade.createOrUpdateUserProfile(userDetails, userProfile);

        assertTrue(result.isEmpty());
        verify(userProfileService, times(1)).createOrUpdateUserProfile(userDetails, userProfile);
    }

    @Test
    public void getUserProfile_Success() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        when(userProfileService.getUserProfile(userDetails)).thenReturn(Optional.of(userProfileDTO));

        Optional<UserProfileDTO> result = userProfileFacade.getUserProfile(userDetails);

        assertEquals(userProfileDTO, result.orElse(null));
        verify(userProfileService, times(1)).getUserProfile(userDetails);
    }

    @Test
    public void getUserProfile_NotFound() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        when(userProfileService.getUserProfile(userDetails)).thenReturn(Optional.empty());

        Optional<UserProfileDTO> result = userProfileFacade.getUserProfile(userDetails);

        assertTrue(result.isEmpty());
        verify(userProfileService, times(1)).getUserProfile(userDetails);
    }

    @Test
    public void getUserProfilesWithCards_Success() {
        when(userProfileService.getAllUserProfiles()).thenReturn(List.of(userProfileDTO));
        when(creditCardService.getCreditCardsByUserId(1L)).thenReturn(List.of(creditCardDTO));

        List<UserCreditCardsDTO> result = userProfileFacade.getUserProfilesWithCards();

        assertEquals(1, result.size());
        assertEquals(userProfileDTO, result.get(0).getUserProfile());
        assertEquals(1, result.get(0).getCreditCards().size());
        assertEquals(creditCardDTO, result.get(0).getCreditCards().get(0));

        verify(userProfileService, times(1)).getAllUserProfiles();
        verify(creditCardService, times(1)).getCreditCardsByUserId(1L);
    }

    @Test
    public void getUserProfilesWithCards_NoProfilesFound() {
        when(userProfileService.getAllUserProfiles()).thenReturn(List.of());

        List<UserCreditCardsDTO> result = userProfileFacade.getUserProfilesWithCards();

        assertTrue(result.isEmpty());
        verify(userProfileService, times(1)).getAllUserProfiles();
        verify(creditCardService, never()).getCreditCardsByUserId(anyLong());
    }

    @Test
    public void getUserProfilesWithCards_NoCreditCards() {
        when(userProfileService.getAllUserProfiles()).thenReturn(List.of(userProfileDTO));
        when(creditCardService.getCreditCardsByUserId(1L)).thenReturn(List.of());

        List<UserCreditCardsDTO> result = userProfileFacade.getUserProfilesWithCards();

        assertEquals(1, result.size());
        assertEquals(userProfileDTO, result.get(0).getUserProfile());
        assertTrue(result.get(0).getCreditCards().isEmpty());

        verify(userProfileService, times(1)).getAllUserProfiles();
        verify(creditCardService, times(1)).getCreditCardsByUserId(1L);
    }
}
