package com.example.bookstore.exception;

public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String fieldName, String message) {
        super(fieldName, message);
    }
}

