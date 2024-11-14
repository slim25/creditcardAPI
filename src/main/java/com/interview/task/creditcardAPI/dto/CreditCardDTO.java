package com.interview.task.creditcardAPI.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardDTO {
    private String maskedCardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cardToken;
}