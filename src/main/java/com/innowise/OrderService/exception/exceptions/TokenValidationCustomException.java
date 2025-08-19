package com.innowise.OrderService.exception.exceptions;

public class TokenValidationCustomException extends RuntimeException {
    public TokenValidationCustomException(String message) {
        super(message);
    }
}
