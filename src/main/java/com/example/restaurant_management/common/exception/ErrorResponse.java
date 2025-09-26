package com.example.restaurant_management.common.exception;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private Integer httpStatus;
    private String errorCode;
    private String message;
    private String traceId;
    private @Builder.Default Boolean success = Boolean.FALSE;
}
