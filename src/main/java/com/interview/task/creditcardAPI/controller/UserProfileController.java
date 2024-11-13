package com.interview.task.creditcardAPI.controller;

import com.interview.task.creditcardAPI.dto.UserCreditCardsDTO;
import com.interview.task.creditcardAPI.dto.UserProfileDTO;
import com.interview.task.creditcardAPI.facade.UserProfileFacade;
import com.interview.task.creditcardAPI.model.UserProfile;
import com.interview.task.creditcardAPI.service.UserActivityLogService;
import com.interview.task.creditcardAPI.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-profiles")
public class UserProfileController {

    private static final Logger LOG = LoggerFactory.getLogger(UserProfileController.class);
    private UserUtils userUtils;
    private UserActivityLogService activityLogService;
    private final UserProfileFacade userProfileFacade;

    @Autowired
    public UserProfileController(UserProfileFacade userProfileFacade, UserUtils userUtils, UserActivityLogService activityLogService) {
        this.userProfileFacade = userProfileFacade;
        this.userUtils = userUtils;
        this.activityLogService = activityLogService;
    }

    @PostMapping
    public ResponseEntity<UserProfileDTO> createOrUpdateUserProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                            @RequestBody UserProfile userProfile) {
        Optional<UserProfileDTO> updatedUserProfile = userProfileFacade.createOrUpdateUserProfile(userDetails, userProfile);
        if (updatedUserProfile.isPresent()) {
            UserProfileDTO updatedUserProfileValue = updatedUserProfile.get();

            activityLogService.logActivity(updatedUserProfileValue.getUserId(), "CREATE_OR_UPDATE_USER_PROFILE",
                    "User created or updated profile");
            LOG.debug("User {} created or updated profile", updatedUserProfileValue.getName());

            return ResponseEntity.ok(updatedUserProfileValue);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<UserProfileDTO> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Optional<UserProfileDTO> userProfile = userProfileFacade.getUserProfile(userDetails);

        if (userProfile.isPresent()) {
            UserProfileDTO userProfileDTO = userProfile.get();

            Long userId = userUtils.getCurrentUserId(userDetails.getUsername());
            activityLogService.logActivity(userId, "GET_USER_PROFILE", "User get profile");
            LOG.debug("User {} get profile", userDetails.getUsername());

            return ResponseEntity.ok(userProfileDTO);
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<UserCreditCardsDTO>> getAllUserProfiles() {
        LOG.debug("Admin accessed all user profiles");
        List<UserCreditCardsDTO> userProfilesWithCards = userProfileFacade.getUserProfilesWithCards();

        Long userId = userUtils.getCurrentUserId();
        activityLogService.logActivity(userId, "GET_ALL_USER_PROFILES", "User fetched all user profiles");
        LOG.debug("User with id {} fetched all user profiles", userId);

        return ResponseEntity.ok(userProfilesWithCards);
    }
}
