package com.interview.task.creditcardAPI.service.impl;

import com.interview.task.creditcardAPI.dto.UserProfileDTO;
import com.interview.task.creditcardAPI.model.User;
import com.interview.task.creditcardAPI.model.UserProfile;
import com.interview.task.creditcardAPI.repository.jpa.UserProfileRepository;
import com.interview.task.creditcardAPI.repository.jpa.UserRepository;
import com.interview.task.creditcardAPI.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserProfileDTO> getAllUserProfiles() {
        return userProfileRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserProfileDTO> createOrUpdateUserProfile(UserDetails userDetails, UserProfile userProfile) {
        Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
        if (user.isEmpty()) {
            return Optional.empty();
        }

        User existingUser = user.get();
        Optional<UserProfile> existingProfile = userProfileRepository.findByUserId(existingUser.getId());

        if (existingProfile.isPresent()) {
            UserProfile profileToUpdate = existingProfile.get();
            profileToUpdate.setName(userProfile.getName());
            profileToUpdate.setEmail(userProfile.getEmail());
            profileToUpdate.setPhone(userProfile.getPhone());
            UserProfile updatedProfile = userProfileRepository.save(profileToUpdate);
            return Optional.of(mapToDTO(updatedProfile));
        } else {
            userProfile.setUser(existingUser);
            UserProfile savedProfile = userProfileRepository.save(userProfile);
            return Optional.of(mapToDTO(savedProfile));
        }
    }

    @Override
    public Optional<UserProfileDTO> getUserProfile(UserDetails userDetails) {
        Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
        if (user.isPresent()) {
            return userProfileRepository.findByUserId(user.get().getId())
                    .map(this::mapToDTO);
        }
        return Optional.empty();
    }

    private UserProfileDTO mapToDTO(UserProfile userProfile) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(userProfile.getId());
        dto.setName(userProfile.getName());
        dto.setEmail(userProfile.getEmail());
        dto.setPhone(userProfile.getPhone());
        return dto;
    }
}
