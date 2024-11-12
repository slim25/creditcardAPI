package com.interview.task.creditcardAPI.controller;

import com.interview.task.creditcardAPI.dto.CreditCardDTO;
import com.interview.task.creditcardAPI.dto.CreditCardRequestDTO;
import com.interview.task.creditcardAPI.model.CreditCard;
import com.interview.task.creditcardAPI.service.CreditCardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-cards")
public class CreditCardController {

    private static final Logger LOG = LoggerFactory.getLogger(CreditCardController.class);

    private final CreditCardService creditCardService;

    @Autowired
    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @PostMapping
    public ResponseEntity<CreditCardDTO> createCreditCard(@RequestBody CreditCardRequestDTO creditCardRequestDTO) throws Exception {
        LOG.debug("Received request to create a new credit card for holder");
        CreditCard savedCard = creditCardService.saveCreditCard(creditCardRequestDTO);

        CreditCardDTO responseDTO = new CreditCardDTO(
                savedCard.getMaskedCardNumber(),
                savedCard.getCardHolderName(),
                savedCard.getExpiryDate(),
                savedCard.getCardToken()
        );

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<CreditCardDTO>> getAllCreditCards() {
        LOG.debug("Received request to fetch all credit cards");

        List<CreditCardDTO> creditCards = creditCardService.getAllCreditCards();

        return ResponseEntity.ok(creditCards);
    }

    @GetMapping("/token/{token}")
    public ResponseEntity<CreditCard> getCreditCardByToken(@PathVariable String token) {
        LOG.debug("Received request to fetch credit card with token: {}", token);
        CreditCard creditCard = creditCardService.getCreditCardByToken(token);
        LOG.debug("Successfully retrieved credit card with token: {}", token);
        return ResponseEntity.ok(creditCard);
    }
}
