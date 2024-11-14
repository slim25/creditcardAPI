package com.interview.task.creditcardAPI.service.impl;

import com.interview.task.creditcardAPI.dto.CreditCardDTO;
import com.interview.task.creditcardAPI.dto.CreditCardRequestDTO;
import com.interview.task.creditcardAPI.exception.DuplicateCreditCardException;
import com.interview.task.creditcardAPI.model.CreditCard;
import com.interview.task.creditcardAPI.model.User;
import com.interview.task.creditcardAPI.repository.jpa.CreditCardRepository;
import com.interview.task.creditcardAPI.repository.jpa.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreditCardServiceImplTest {

    @InjectMocks
    private CreditCardServiceImpl creditCardService;

    @Mock
    private CreditCardRepository creditCardRepository;

    @Mock
    private UserRepository userRepository;

    private User testUser;
    private CreditCard creditCard;
    private CreditCardRequestDTO creditCardRequestDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        creditCard = new CreditCard();
        creditCard.setId(1L);
        creditCard.setCardToken("token123");
        creditCard.setMaskedCardNumber("**** **** **** 1234");
        creditCard.setCardHolderName("Test User");
        creditCard.setExpiryDate("12/24");
        creditCard.setUser(testUser);

        creditCardRequestDTO = new CreditCardRequestDTO();
        creditCardRequestDTO.setCardToken("token123");
        creditCardRequestDTO.setCardHolderName("Test User");
        creditCardRequestDTO.setExpiryDate("12/24");
        creditCardRequestDTO.setLast4Digits("1234");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void saveCreditCard_Success() throws Exception {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(creditCardRepository.findByCardTokenAndUser_Id("token123", 1L)).thenReturn(Optional.empty());
        when(creditCardRepository.save(any(CreditCard.class))).thenReturn(creditCard);

        CreditCardDTO result = creditCardService.saveCreditCard(creditCardRequestDTO);

        assertEquals("**** **** **** 1234", result.getMaskedCardNumber());
        assertEquals("Test User", result.getCardHolderName());
        assertEquals("12/24", result.getExpiryDate());
        assertEquals("token123", result.getCardToken());

        verify(creditCardRepository, times(1)).save(any(CreditCard.class));
    }

    @Test
    void saveCreditCard_Duplicate() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(creditCardRepository.findByCardTokenAndUser_Id("token123", 1L)).thenReturn(Optional.of(creditCard));

        assertThrows(DuplicateCreditCardException.class, () -> creditCardService.saveCreditCard(creditCardRequestDTO));
    }

    @Test
    void saveCreditCard_UserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> creditCardService.saveCreditCard(creditCardRequestDTO));
    }

    @Test
    void getCreditCardByToken_Success() {
        when(creditCardRepository.findByCardToken("token123")).thenReturn(Optional.of(creditCard));

        CreditCard result = creditCardService.getCreditCardByToken("token123");

        assertEquals("**** **** **** 1234", result.getMaskedCardNumber());
        assertEquals("token123", result.getCardToken());
        verify(creditCardRepository, times(1)).findByCardToken("token123");
    }

    @Test
    void getCreditCardByToken_NotFound() {
        when(creditCardRepository.findByCardToken("token123")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> creditCardService.getCreditCardByToken("token123"));
    }

    @Test
    void getAllCreditCards() {
        when(creditCardRepository.findAll()).thenReturn(List.of(creditCard));

        List<CreditCardDTO> result = creditCardService.getAllCreditCards();

        assertEquals(1, result.size());
        assertEquals("**** **** **** 1234", result.get(0).getMaskedCardNumber());
        verify(creditCardRepository, times(1)).findAll();
    }

    @Test
    void getCreditCardsByUserId() {
        when(creditCardRepository.findByUser_Id(1L)).thenReturn(List.of(creditCard));

        List<CreditCardDTO> result = creditCardService.getCreditCardsByUserId(1L);

        assertEquals(1, result.size());
        assertEquals("**** **** **** 1234", result.get(0).getMaskedCardNumber());
        verify(creditCardRepository, times(1)).findByUser_Id(1L);
    }

    @Test
    void deleteCreditCardByToken_Success() {
        when(creditCardRepository.findByCardTokenAndUser_Id("token123", 1L)).thenReturn(Optional.of(creditCard));

        boolean result = creditCardService.deleteCreditCardByToken("token123", 1L);

        assertTrue(result);
        verify(creditCardRepository, times(1)).delete(creditCard);
    }

    @Test
    void deleteCreditCardByToken_NotFound() {
        when(creditCardRepository.findByCardTokenAndUser_Id("token123", 1L)).thenReturn(Optional.empty());

        boolean result = creditCardService.deleteCreditCardByToken("token123", 1L);

        assertFalse(result);
        verify(creditCardRepository, never()).delete(any(CreditCard.class));
    }
}
