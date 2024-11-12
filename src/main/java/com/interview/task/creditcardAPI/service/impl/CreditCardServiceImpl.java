package com.interview.task.creditcardAPI.service.impl;

import com.interview.task.creditcardAPI.dto.CreditCardDTO;
import com.interview.task.creditcardAPI.dto.CreditCardRequestDTO;
import com.interview.task.creditcardAPI.model.CreditCard;
import com.interview.task.creditcardAPI.model.User;
import com.interview.task.creditcardAPI.repository.jpa.CreditCardRepository;
import com.interview.task.creditcardAPI.repository.jpa.UserRepository;
import com.interview.task.creditcardAPI.service.CreditCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class CreditCardServiceImpl implements CreditCardService {

    private static final Logger LOG = LoggerFactory.getLogger(CreditCardService.class);

    private final CreditCardRepository creditCardRepository;
    private final UserRepository userRepository;

    @Autowired
    public CreditCardServiceImpl(CreditCardRepository creditCardRepository, UserRepository userRepository) {
        this.creditCardRepository = creditCardRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CreditCard saveCreditCard(CreditCardRequestDTO dto) throws Exception {
        String cardToken = dto.getCardToken();
        String last4Digits = dto.getLast4Digits();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("User not found with username: " + username));

        CreditCard creditCard = new CreditCard();
        creditCard.setMaskedCardNumber("**** **** **** " + last4Digits);
        creditCard.setCardHolderName(dto.getCardHolderName());
        creditCard.setExpiryDate(dto.getExpiryDate());
        creditCard.setUser(user);
        creditCard.setCardToken(cardToken);

        return creditCardRepository.save(creditCard);
    }

    @Override
    public CreditCard getCreditCard(Long id) {
        LOG.debug("Fetching credit card with ID: {}", id);
        return creditCardRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Credit card not found with ID: " + id));
    }

    @Override
    public CreditCard getCreditCardByToken(String token) {
        return creditCardRepository.findByCardToken(token)
                .orElseThrow(() -> new NoSuchElementException("Credit card not found with token: " + token));
    }

    @Override
    public List<CreditCardDTO> getAllCreditCards() {
        LOG.debug("Received request to fetch all credit cards");

        List<CreditCardDTO> creditCards = creditCardRepository.findAll().stream()
                .map(card -> new CreditCardDTO(
                        card.getMaskedCardNumber(),
                        card.getCardHolderName(),
                        card.getExpiryDate(),
                        card.getCardToken()))
                .collect(Collectors.toList());

        return creditCards;
    }


    @Override
    public List<CreditCardDTO> getAllCreditCardsForUser(User user) {
        List<CreditCard> creditCards = creditCardRepository.findByUser(user);
        return creditCards.stream()
                .map(card -> new CreditCardDTO(card.getMaskedCardNumber(), card.getCardHolderName(), card.getExpiryDate(), card.getCardToken()))
                .collect(Collectors.toList());
    }
}