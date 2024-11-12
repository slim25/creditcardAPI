package com.interview.task.creditcardAPI.service;

import com.interview.task.creditcardAPI.dto.CreditCardDTO;
import com.interview.task.creditcardAPI.dto.CreditCardRequestDTO;
import com.interview.task.creditcardAPI.model.CreditCard;
import com.interview.task.creditcardAPI.model.User;

import java.util.List;

public interface CreditCardService {
    CreditCard saveCreditCard(CreditCardRequestDTO creditCardRequestDTO) throws Exception;
    List<CreditCardDTO> getAllCreditCards();
    CreditCard getCreditCard(Long id) throws Exception;
    CreditCard getCreditCardByToken(String token);
    List<CreditCardDTO> getAllCreditCardsForUser(User user);
}
