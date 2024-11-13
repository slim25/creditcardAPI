package com.interview.task.creditcardAPI.service;

import com.interview.task.creditcardAPI.dto.CreditCardDTO;
import com.interview.task.creditcardAPI.dto.CreditCardRequestDTO;
import com.interview.task.creditcardAPI.model.CreditCard;

import java.util.List;

public interface CreditCardService {
    CreditCardDTO saveCreditCard(CreditCardRequestDTO creditCardRequestDTO) throws Exception;
    List<CreditCardDTO> getAllCreditCards();
    CreditCard getCreditCard(Long id) throws Exception;
    CreditCard getCreditCardByToken(String token);

    List<CreditCardDTO> getCreditCardsByUserId(Long userId);

    void deleteCreditCardByToken(String token);
}
