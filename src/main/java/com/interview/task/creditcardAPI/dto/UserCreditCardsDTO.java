package com.interview.task.creditcardAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserCreditCardsDTO {
    private UserProfileDTO userProfile;
    private List<CreditCardDTO> creditCards;
}
