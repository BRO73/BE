package com.example.restaurant_management.common.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;

@Getter
@Setter
public class InputValidationException extends RuntimeException {
    private final transient Map<String, Object> error;

    public InputValidationException(String key, String value) {
        this.error = Map.of(key, value);
    }

    public InputValidationException(Map<String, Object> error) {
        this.error = error;
    }

    public InputValidationException(BindingResult bindingResult) {
        this.error = new HashMap<>();
        bindingResult.getFieldErrors().forEach(fieldError -> this.error.put(fieldError.getField(), fieldError.getDefaultMessage()));
    }

    public InputValidationException(String value) {
        this.error = Map.of("error", value);
    }
}
