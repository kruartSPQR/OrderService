package com.innowise.OrderService.exception;

import com.innowise.common.exception.ResourceNotFoundCustomException;
import com.innowise.common.exception.DuplicateResourceCustomException;

import com.innowise.common.exception.TokenValidationCustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundCustomException.class)
    public ResponseEntity<String> resourceNotFoundCustomException(ResourceNotFoundCustomException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateResourceCustomException.class)
    public ResponseEntity<String> duplicateResourceCustomException(DuplicateResourceCustomException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TokenValidationCustomException.class)
    public ResponseEntity<String> tokenValidationCustomException(TokenValidationCustomException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
