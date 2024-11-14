package com.interview.task.creditcardAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreditCardsDTO {
    private UserProfileDTO userProfile;
    private List<CreditCardDTO> creditCards;
}
