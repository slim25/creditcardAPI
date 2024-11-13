package com.interview.task.creditcardAPI.facade;

import com.interview.task.creditcardAPI.dto.UserCreditCardsDTO;
import com.interview.task.creditcardAPI.dto.UserProfileDTO;
import com.interview.task.creditcardAPI.model.UserProfile;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UserProfileFacade {
    Optional<UserProfileDTO> createOrUpdateUserProfile(UserDetails userDetails, UserProfile userProfile);

    Optional<UserProfileDTO> getUserProfile(UserDetails userDetails);

    List<UserCreditCardsDTO> getUserProfilesWithCards();

}
