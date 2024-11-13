package com.interview.task.creditcardAPI.repository.jpa;

import com.interview.task.creditcardAPI.model.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    Optional<CreditCard> findByCardToken(String cardToken);
    List<CreditCard> findByUser_Id(Long userId);
    Optional<CreditCard> findByCardTokenAndUser_Id(String cardToken, Long userId);
}
