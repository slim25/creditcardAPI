package com.interview.task.creditcardAPI.utils;

import com.interview.task.creditcardAPI.dto.CreditCardRequestDTO;
import com.interview.task.creditcardAPI.dto.DeleteCreditCardRequestDTO;
import com.interview.task.creditcardAPI.dto.LoginRequest;
import com.interview.task.creditcardAPI.exception.InvalidRequestDataException;
import com.interview.task.creditcardAPI.model.User;
import com.interview.task.creditcardAPI.model.UserProfile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class ValidationUtils {

    public void validateUserRegistration(User user) {
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            throw new InvalidRequestDataException("Incoming data is invalid");
        }
    }

    public void validateUserLogin(LoginRequest loginRequest) {
        if (StringUtils.isBlank(loginRequest.getUsername()) || StringUtils.isBlank(loginRequest.getPassword())) {
            throw new InvalidRequestDataException("Incoming data is invalid");
        }
    }

    public void validateCreateCreditCardRequest(CreditCardRequestDTO creditCardRequestDTO) {
        if (StringUtils.isBlank(creditCardRequestDTO.getCardToken()) || StringUtils.isBlank(creditCardRequestDTO.getExpiryDate()) ||
                            StringUtils.isBlank(creditCardRequestDTO.getCardHolderName())) {
            throw new InvalidRequestDataException("Incoming data is invalid");
        }
    }

    public void validateGetUserCreditCards(UserDetails userDetails) {
        if (userDetails == null) {
            throw new InvalidRequestDataException("Incoming data is invalid");
        }
    }

    public void validateDeleteCreditCardByToken(DeleteCreditCardRequestDTO request) {
        if (StringUtils.isBlank(request.getToken()) || request.getUserId() == null || request.getUserId() <= 0) {
            throw new InvalidRequestDataException("Incoming data is invalid");
        }
    }

    public void validateCreateOrUpdateUserProfile(UserDetails userDetails, UserProfile userProfile) {
        if (userDetails == null || userProfile == null) {
            throw new InvalidRequestDataException("Incoming data is invalid");
        }
    }

    public void validateGetUserProfile(UserDetails userDetails) {
        if (userDetails == null) {
            throw new InvalidRequestDataException("Incoming data is invalid");
        }
    }
}
