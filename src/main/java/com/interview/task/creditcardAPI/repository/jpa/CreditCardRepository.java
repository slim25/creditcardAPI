package com.interview.task.creditcardAPI.repository.jpa;

import com.interview.task.creditcardAPI.model.CreditCard;
import com.interview.task.creditcardAPI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    Optional<CreditCard> findByCardToken(String cardToken);
    List<CreditCard> findByUser(User user);
}
