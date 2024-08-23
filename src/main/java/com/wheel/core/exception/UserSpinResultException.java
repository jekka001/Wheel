package com.wheel.core.exception;

public class UserSpinResultException extends RuntimeException {
    private static final String USER_TRY_CHECK_NOT_HIS_SPIN_RESULT = "User try check not his spin result ";
    private static final String USER_TRY_TWICE_GET_REWARD = "User try twice get reward for spin - ";

    public UserSpinResultException() {
    }

    public UserSpinResultException(String spinId) {
        super(USER_TRY_TWICE_GET_REWARD + spinId);
    }

    public UserSpinResultException(String userId, String spinId) {
        super(USER_TRY_CHECK_NOT_HIS_SPIN_RESULT + "userId -" + userId + " spinId - " + spinId);
    }

    public UserSpinResultException(String message, Throwable cause) {
        super(message, cause);
    }
}
