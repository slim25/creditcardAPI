package com.interview.task.creditcardAPI.service;

import com.interview.task.creditcardAPI.dto.UserProfileDTO;
import com.interview.task.creditcardAPI.model.UserProfile;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UserProfileService {
    List<UserProfileDTO> getAllUserProfiles();
    Optional<UserProfileDTO> createOrUpdateUserProfile(UserDetails userDetails, UserProfile userProfile);
    Optional<UserProfileDTO> getUserProfile(UserDetails userDetails);
}
