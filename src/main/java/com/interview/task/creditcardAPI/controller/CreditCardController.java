package com.interview.task.creditcardAPI.controller;

import com.interview.task.creditcardAPI.dto.CreditCardDTO;
import com.interview.task.creditcardAPI.dto.CreditCardRequestDTO;
import com.interview.task.creditcardAPI.model.CreditCard;
import com.interview.task.creditcardAPI.repository.jpa.UserRepository;
import com.interview.task.creditcardAPI.service.CreditCardService;
import com.interview.task.creditcardAPI.service.UserActivityLogService;
import com.interview.task.creditcardAPI.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-cards")
public class CreditCardController {

    private static final Logger LOG = LoggerFactory.getLogger(CreditCardController.class);

    private final CreditCardService creditCardService;
    private final UserRepository userRepository;

    private UserActivityLogService activityLogService;
    private UserUtils userUtils;

    @Autowired
    public CreditCardController(CreditCardService creditCardService, UserRepository userRepository,
                                UserActivityLogService activityLogService, UserUtils userUtils) {
        this.creditCardService = creditCardService;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
        this.userUtils = userUtils;
    }

    @PostMapping
    public ResponseEntity<CreditCardDTO> createCreditCard(@RequestBody CreditCardRequestDTO creditCardRequestDTO) throws Exception {
        LOG.debug("Received request to create a new credit card for holder");
        CreditCardDTO savedCardResponseDTO = creditCardService.saveCreditCard(creditCardRequestDTO);

        Long userId = userUtils.getCurrentUserId();
        activityLogService.logActivity(userId, "ADD_CREDIT_CARD", "User added a new credit card");
        LOG.debug("User with id {} added a new credit card", userId);

        return ResponseEntity.ok(savedCardResponseDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CreditCardDTO>> getAllCreditCards() {
        LOG.debug("Received request to fetch all credit cards");

        List<CreditCardDTO> creditCards = creditCardService.getAllCreditCards();

        Long userId = userUtils.getCurrentUserId();
        activityLogService.logActivity(userId, "GET_ALL_CREDIT_CARD", "User fetched all credit cards");
        LOG.debug("User with id {} fetched all credit cards", userId);

        return ResponseEntity.ok(creditCards);
    }

    @GetMapping
    public ResponseEntity<List<CreditCardDTO>> getUserCreditCards(@AuthenticationPrincipal UserDetails userDetails) {

        Long userId = userUtils.getCurrentUserId(userDetails.getUsername());
        List<CreditCardDTO> creditCards = creditCardService.getCreditCardsByUserId(userId);

        activityLogService.logActivity(userId, "GET_USER_CREDIT_CARDS", "User fetched their credit cards");
        LOG.debug("User {} fetched their credit cards", userDetails.getUsername());

        return ResponseEntity.ok(creditCards);
    }

    @GetMapping("/token/{token}")
    public ResponseEntity<CreditCard> getCreditCardByToken(@PathVariable String token) {
        CreditCard creditCard = creditCardService.getCreditCardByToken(token);

        Long userId = userUtils.getCurrentUserId(creditCard.getUser().getUsername());
        activityLogService.logActivity(userId, "GET_CREDIT_CARD_BY_TOKEN", "User fetched credit card by token");

        LOG.debug("Successfully retrieved credit card with token: {}", token);
        return ResponseEntity.ok(creditCard);
    }

    @DeleteMapping("/{token}")
    public ResponseEntity<Void> deleteCreditCardByToken(@PathVariable String token) {
        creditCardService.deleteCreditCardByToken(token);

        Long userId = userUtils.getCurrentUserId();
        activityLogService.logActivity(userId, "DELETE_CREDIT_CARD_BY_TOKEN", "Successfully deleted credit card with token");

        LOG.debug("Successfully deleted credit card with token: {}", token);
        return ResponseEntity.noContent().build();
    }
}
