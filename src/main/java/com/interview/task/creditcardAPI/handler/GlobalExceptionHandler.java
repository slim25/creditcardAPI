package com.interview.task.creditcardAPI.handler;

import com.interview.task.creditcardAPI.exception.DuplicateCreditCardException;
import com.interview.task.creditcardAPI.exception.InvalidTokenException;
import com.interview.task.creditcardAPI.exception.RefreshTokenExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
        LOG.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        LOG.error("An unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred. Please try again later.");
    }

    @ExceptionHandler(DuplicateCreditCardException.class)
    public ResponseEntity<String> handleDuplicateCreditCardException(DuplicateCreditCardException ex) {
        LOG.error("An unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler({RefreshTokenExpiredException.class, InvalidTokenException.class})
    public ResponseEntity<String> handleRefreshTokenExpiredException(RefreshTokenExpiredException ex) {
        LOG.error("Refresh token expired exception", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
}
