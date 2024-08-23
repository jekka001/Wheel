package com.wheel.core.exception;

public class SlotsNotFoundException extends NotFoundException {
    private static final String SLOTS_NOT_FOUND = "Not found slots by reelId - ";

    public SlotsNotFoundException() {
    }

    public SlotsNotFoundException(String reelId) {
        super(SLOTS_NOT_FOUND + reelId);
    }

    public SlotsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
