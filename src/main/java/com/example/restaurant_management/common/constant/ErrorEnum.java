package com.example.restaurant_management.common.constant;

import lombok.Getter;

@Getter
public enum ErrorEnum {
    INVALID_CREDENTIALS(400, "invalid_credentials", "Incorrect username or password"),
    ACCESS_DENIED(403, "access_denied", "You do not have permission to access this resource"),
    SESSION_EXPIRED(401, "session_expired", "Session has expired. Please log in again."),
    INVALID_INPUT_COMMON(400, "invalid_input", "%s"),
    INVALID_INPUT(400, "invalid_input", "Invalid input data"),
    DUPLICATE_ERROR(400, "duplicate_error", "Data duplication error"),
    INTERNAL_SERVER_ERROR(500, "internal_server_error",
            "An error occurred. Please try again later or contact the administrator for support."),
    INVALID_ACCESS_TOKEN(401, "invalid_access_token",
            "Session has expired. Please log in again."),
    USER_EXIST(400, "user_exist", "User already exists"),
    USER_NOT_FOUND(400, "user_not_found", "User not found"),
    STORE_NOT_FOUND(400, "store_not_found", "Store not found"),
    PASSWORD_INCORRECT(400, "password_incorrect", "Password is incorrect"),
    USER_DISABLED(400, "user_disabled", "User is disabled");
    private final Integer httpStatus;
    private final String code;
    private final String message;

    ErrorEnum(Integer httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

}
