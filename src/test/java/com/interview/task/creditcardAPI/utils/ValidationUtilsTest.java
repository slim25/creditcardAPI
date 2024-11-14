package com.interview.task.creditcardAPI.utils;

import com.interview.task.creditcardAPI.dto.CreditCardRequestDTO;
import com.interview.task.creditcardAPI.dto.DeleteCreditCardRequestDTO;
import com.interview.task.creditcardAPI.dto.LoginRequest;
import com.interview.task.creditcardAPI.exception.InvalidRequestDataException;
import com.interview.task.creditcardAPI.model.User;
import com.interview.task.creditcardAPI.model.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class ValidationUtilsTest {

    private ValidationUtils validationUtils;

    @BeforeEach
    void setUp() {
        validationUtils = new ValidationUtils();
    }

    @Test
    void validateUserRegistration_ValidUser_ShouldNotThrowException() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("securePassword");

        assertDoesNotThrow(() -> validationUtils.validateUserRegistration(user));
    }

    @Test
    void validateUserRegistration_InvalidUser_ShouldThrowException() {
        User invalidUser = new User();

        assertThrows(InvalidRequestDataException.class, () -> validationUtils.validateUserRegistration(invalidUser));
    }

    @Test
    void validateUserLogin_ValidLoginRequest_ShouldNotThrowException() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("securePassword");

        assertDoesNotThrow(() -> validationUtils.validateUserLogin(loginRequest));
    }

    @Test
    void validateUserLogin_InvalidLoginRequest_ShouldThrowException() {
        LoginRequest invalidRequest = new LoginRequest();

        assertThrows(InvalidRequestDataException.class, () -> validationUtils.validateUserLogin(invalidRequest));
    }

    @Test
    void validateCreateCreditCardRequest_ValidCreditCardRequest_ShouldNotThrowException() {
        CreditCardRequestDTO requestDTO = new CreditCardRequestDTO();
        requestDTO.setCardToken("card123");
        requestDTO.setExpiryDate("12/25");
        requestDTO.setCardHolderName("John Doe");

        assertDoesNotThrow(() -> validationUtils.validateCreateCreditCardRequest(requestDTO));
    }

    @Test
    void validateCreateCreditCardRequest_InvalidCreditCardRequest_ShouldThrowException() {
        CreditCardRequestDTO invalidRequest = new CreditCardRequestDTO(); // missing fields

        assertThrows(InvalidRequestDataException.class, () -> validationUtils.validateCreateCreditCardRequest(invalidRequest));
    }

    @Test
    void validateGetUserCreditCards_ValidUserDetails_ShouldNotThrowException() {
        UserDetails userDetails = mock(UserDetails.class);

        assertDoesNotThrow(() -> validationUtils.validateGetUserCreditCards(userDetails));
    }

    @Test
    void validateGetUserCreditCards_NullUserDetails_ShouldThrowException() {
        assertThrows(InvalidRequestDataException.class, () -> validationUtils.validateGetUserCreditCards(null));
    }

    @Test
    void validateDeleteCreditCardByToken_ValidRequest_ShouldNotThrowException() {
        DeleteCreditCardRequestDTO requestDTO = new DeleteCreditCardRequestDTO();
        requestDTO.setToken("token123");
        requestDTO.setUserId(1L);

        assertDoesNotThrow(() -> validationUtils.validateDeleteCreditCardByToken(requestDTO));
    }

    @Test
    void validateDeleteCreditCardByToken_InvalidRequest_ShouldThrowException() {
        DeleteCreditCardRequestDTO invalidRequest = new DeleteCreditCardRequestDTO();

        assertThrows(InvalidRequestDataException.class, () -> validationUtils.validateDeleteCreditCardByToken(invalidRequest));
    }

    @Test
    void validateCreateOrUpdateUserProfile_ValidProfile_ShouldNotThrowException() {
        UserDetails userDetails = mock(UserDetails.class);
        UserProfile userProfile = new UserProfile();

        assertDoesNotThrow(() -> validationUtils.validateCreateOrUpdateUserProfile(userDetails, userProfile));
    }

    @Test
    void validateCreateOrUpdateUserProfile_InvalidProfile_ShouldThrowException() {
        assertThrows(InvalidRequestDataException.class, () -> validationUtils.validateCreateOrUpdateUserProfile(null, null));
    }

    @Test
    void validateGetUserProfile_ValidUserDetails_ShouldNotThrowException() {
        UserDetails userDetails = mock(UserDetails.class);

        assertDoesNotThrow(() -> validationUtils.validateGetUserProfile(userDetails));
    }

    @Test
    void validateGetUserProfile_NullUserDetails_ShouldThrowException() {
        assertThrows(InvalidRequestDataException.class, () -> validationUtils.validateGetUserProfile(null));
    }
}
