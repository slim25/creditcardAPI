package com.interview.task.creditcardAPI.dto;

import lombok.Data;

@Data
public class CreditCardRequestDTO {
    private String cardHolderName;
    private String expiryDate;
    private String cardToken;
    private String last4Digits;
}

