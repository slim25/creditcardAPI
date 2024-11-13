package com.interview.task.creditcardAPI.facade.impl;

import com.interview.task.creditcardAPI.dto.CreditCardDTO;
import com.interview.task.creditcardAPI.dto.UserCreditCardsDTO;
import com.interview.task.creditcardAPI.dto.UserProfileDTO;
import com.interview.task.creditcardAPI.facade.UserProfileFacade;
import com.interview.task.creditcardAPI.model.UserProfile;
import com.interview.task.creditcardAPI.service.CreditCardService;
import com.interview.task.creditcardAPI.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserProfileFacadeImpl implements UserProfileFacade {

    private final UserProfileService userProfileService;
    private final CreditCardService creditCardService;

    @Autowired
    public UserProfileFacadeImpl(UserProfileService userProfileService, CreditCardService creditCardService) {
        this.userProfileService = userProfileService;
        this.creditCardService = creditCardService;
    }

    @Override
    public Optional<UserProfileDTO> createOrUpdateUserProfile(UserDetails userDetails, UserProfile userProfile) {
        return userProfileService.createOrUpdateUserProfile(userDetails, userProfile);
    }

    @Override
    public Optional<UserProfileDTO> getUserProfile(UserDetails userDetails) {
        return userProfileService.getUserProfile(userDetails);
    }

    @Override
    public List<UserCreditCardsDTO> getUserProfilesWithCards() {
        List<UserCreditCardsDTO> userProfilesWithCards = userProfileService.getAllUserProfiles().stream()
                .map(userProfile -> {
                    List<CreditCardDTO> creditCards = creditCardService.getCreditCardsByUserId(userProfile.getUserId());
                    return new UserCreditCardsDTO(userProfile, creditCards);
                })
                .collect(Collectors.toList());
        return userProfilesWithCards;
    }
}
