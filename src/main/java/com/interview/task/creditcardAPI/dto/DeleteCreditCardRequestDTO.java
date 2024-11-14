package com.interview.task.creditcardAPI.dto;

import lombok.Data;

@Data
public class DeleteCreditCardRequestDTO {
    private String token;
    private Long userId;
}
