package com.interview.task.creditcardAPI.controller;

import com.interview.task.creditcardAPI.dto.UserProfileDTO;
import com.interview.task.creditcardAPI.model.UserProfile;
import com.interview.task.creditcardAPI.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-profiles")
public class UserProfileController {

    private static final Logger LOG = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private UserProfileService userProfileService;

    @PostMapping
    public ResponseEntity<UserProfileDTO> createUserProfile(@AuthenticationPrincipal UserDetails userDetails,
                                                            @RequestBody UserProfile userProfile) {
        return userProfileService.createOrUpdateUserProfile(userDetails, userProfile)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping
    public ResponseEntity<UserProfileDTO> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return userProfileService.getUserProfile(userDetails)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<UserProfileDTO>> getAllUserProfiles() {
        LOG.debug("Admin accessed all user profiles");
        List<UserProfileDTO> profiles = userProfileService.getAllUserProfiles();
        return ResponseEntity.ok(profiles);
    }
}
