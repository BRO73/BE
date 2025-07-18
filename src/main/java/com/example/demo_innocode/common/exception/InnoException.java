package com.example.demo_innocode.common.exception;

import com.example.demo_innocode.common.constant.ErrorEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InnoException extends RuntimeException {

    private final Integer httpStatus;
    private final String errorCode;
    private final String message;

    public InnoException(String msg) {
        this(ErrorEnum.INVALID_INPUT_COMMON, msg);
    }

    public InnoException(ErrorResponse errorResponse) {
        super(errorResponse.getMessage());
        this.httpStatus = errorResponse.getHttpStatus();
        this.errorCode = errorResponse.getErrorCode();
        this.message = errorResponse.getMessage();
    }

    public InnoException(ErrorEnum errorEnum, String... args) {
        super(String.format(errorEnum.getMessage(), (Object[]) args));
        this.httpStatus = errorEnum.getHttpStatus();
        this.errorCode = errorEnum.getCode();
        this.message = String.format(errorEnum.getMessage(), (Object[]) args);
    }

    public InnoException(ErrorEnum errorEnum) {
        super(errorEnum.getMessage());
        this.httpStatus = errorEnum.getHttpStatus();
        this.errorCode = errorEnum.getCode();
        this.message = errorEnum.getMessage();
    }

    public ErrorResponse convertToErrorResponse() {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setHttpStatus(this.httpStatus);
        errorResponse.setErrorCode(this.errorCode);
        errorResponse.setMessage(this.getMessage());
        errorResponse.setSuccess(Boolean.FALSE);
        return errorResponse;
    }
}
