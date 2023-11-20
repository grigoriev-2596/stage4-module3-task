package com.mjc.school.service.exception;

public class JwtAuthenticationException extends BaseException {

    public JwtAuthenticationException(String message, int errorCode, String errorMessage) {
        super(message, errorCode, errorMessage);
    }

    public JwtAuthenticationException(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
