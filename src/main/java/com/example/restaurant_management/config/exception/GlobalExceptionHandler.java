package com.example.restaurant_management.config.exception;


import com.example.restaurant_management.common.exception.promotion.PromotionValidationException;
import com.example.restaurant_management.common.exception.promotion.PromotionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PromotionNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePromotionNotFound(
            PromotionNotFoundException ex, WebRequest request
    ) {
        Map<String, String> body = Map.of("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND); // 404
    }

    @ExceptionHandler(PromotionValidationException.class)
    public ResponseEntity<Map<String, String>> handlePromotionValidation(
            PromotionValidationException ex, WebRequest request
    ) {
        Map<String, String> body = Map.of("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST); // 400
    }

}
