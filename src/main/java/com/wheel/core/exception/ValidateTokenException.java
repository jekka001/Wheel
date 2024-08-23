package com.wheel.core.exception;

public class ValidateTokenException extends RuntimeException {
    private static final String TOKEN_NOT_VALID = "Token is not valid";

    public ValidateTokenException() {
        super(TOKEN_NOT_VALID);
    }

    public ValidateTokenException(String message) {
        super(message);
    }

    public ValidateTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
