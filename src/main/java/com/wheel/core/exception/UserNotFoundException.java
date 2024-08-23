package com.wheel.core.exception;

public class UserNotFoundException extends NotFoundException {
    private static final String USER_NOT_FOUND = "User not found - ";

    public UserNotFoundException() {
    }

    public UserNotFoundException(String field) {
        super(USER_NOT_FOUND + field);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
