package com.interview.task.creditcardAPI.exception;

public class DuplicateCreditCardException extends RuntimeException {
    public DuplicateCreditCardException(String message) {
        super(message);
    }
}
