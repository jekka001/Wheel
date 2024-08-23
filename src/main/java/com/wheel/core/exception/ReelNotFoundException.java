package com.wheel.core.exception;

public class ReelNotFoundException extends NotFoundException {
    private static final String REEL_NOT_FOUND = "Reel not found by spinId - ";

    public ReelNotFoundException() {
    }

    public ReelNotFoundException(String spinId) {
        super(REEL_NOT_FOUND + spinId);
    }

    public ReelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
