package com.innowise.OrderService.exception.exceptions;

public class ResourceNotFoundCustomException extends RuntimeException {
    public ResourceNotFoundCustomException(String message) {
        super(message);
    }
}
