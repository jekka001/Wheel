package com.wheel.core.exception;

public class RoleNotFoundException extends NotFoundException {
    private static final String ROLE_NOT_FOUND = "Role not found by name - ";

    public RoleNotFoundException() {
    }

    public RoleNotFoundException(String name) {
        super(ROLE_NOT_FOUND + name);
    }

    public RoleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
