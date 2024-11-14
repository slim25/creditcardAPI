package com.interview.task.creditcardAPI.controller;

import com.interview.task.creditcardAPI.dto.CreditCardDTO;
import com.interview.task.creditcardAPI.dto.CreditCardRequestDTO;
import com.interview.task.creditcardAPI.dto.DeleteCreditCardRequestDTO;
import com.interview.task.creditcardAPI.service.CreditCardService;
import com.interview.task.creditcardAPI.service.UserActivityLogService;
import com.interview.task.creditcardAPI.utils.UserUtils;
import com.interview.task.creditcardAPI.utils.ValidationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreditCardControllerTest {

    @InjectMocks
    private CreditCardController creditCardController;

    @Mock
    private CreditCardService creditCardService;

    @Mock
    private UserActivityLogService activityLogService;

    @Mock
    private UserUtils userUtils;

    @Mock
    private ValidationUtils validationUtils;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateCreditCardSuccess() throws Exception {
        CreditCardRequestDTO requestDTO = new CreditCardRequestDTO();
        requestDTO.setCardHolderName("John Doe");
        requestDTO.setExpiryDate("12/25");
        requestDTO.setCardToken("token123");
        requestDTO.setLast4Digits("1234");

        CreditCardDTO responseDTO = new CreditCardDTO("**** **** **** 1234", "John Doe", "12/25", "token123");
        when(creditCardService.saveCreditCard(any(CreditCardRequestDTO.class))).thenReturn(responseDTO);

        ResponseEntity<CreditCardDTO> response = creditCardController.createCreditCard(requestDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(activityLogService, times(1)).logActivity(any(), eq("ADD_CREDIT_CARD"), anyString());
    }

    @Test
    public void testGetUserCreditCards() {
        CreditCardDTO cardDTO = new CreditCardDTO("**** **** **** 1234", "John Doe", "12/25", "token123");
        when(userUtils.getCurrentUserId(anyString())).thenReturn(1L);
        when(creditCardService.getCreditCardsByUserId(anyLong())).thenReturn(List.of(cardDTO));

        ResponseEntity<List<CreditCardDTO>> response = creditCardController.getUserCreditCards(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.singletonList(cardDTO), response.getBody());
        verify(activityLogService, times(1)).logActivity(any(), eq("GET_USER_CREDIT_CARDS"), anyString());
    }

    @Test
    public void testGetAllCreditCards() {
        CreditCardDTO cardDTO = new CreditCardDTO("**** **** **** 1234", "John Doe", "12/25", "token123");
        when(creditCardService.getAllCreditCards()).thenReturn(List.of(cardDTO));

        ResponseEntity<List<CreditCardDTO>> response = creditCardController.getAllCreditCards();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Collections.singletonList(cardDTO), response.getBody());
        verify(activityLogService, times(1)).logActivity(any(), eq("GET_ALL_CREDIT_CARD"), anyString());
    }

    @Test
    public void testDeleteCreditCardSuccess() {
        DeleteCreditCardRequestDTO request = new DeleteCreditCardRequestDTO();
        request.setToken("token123");
        request.setUserId(1L);

        when(creditCardService.deleteCreditCardByToken(anyString(), anyLong())).thenReturn(true);

        ResponseEntity<Void> response = creditCardController.deleteCreditCardByToken(request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(activityLogService, times(1)).logActivity(any(), eq("DELETE_CREDIT_CARD_BY_TOKEN"), anyString());
    }

    @Test
    public void testDeleteCreditCardFailure() {
        DeleteCreditCardRequestDTO request = new DeleteCreditCardRequestDTO();
        request.setToken("nonexistentToken");
        request.setUserId(1L);

        when(creditCardService.deleteCreditCardByToken(anyString(), anyLong())).thenReturn(false);

        ResponseEntity<Void> response = creditCardController.deleteCreditCardByToken(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
