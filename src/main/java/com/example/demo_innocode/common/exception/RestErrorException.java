package com.example.demo_innocode.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RestErrorException extends RuntimeException {
    private final int httpStatus;
    private final String body;
}
