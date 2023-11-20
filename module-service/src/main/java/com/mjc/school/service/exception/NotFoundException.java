package com.mjc.school.service.exception;

public class NotFoundException extends BaseException {

    public NotFoundException(String message, int errorCode, String errorMessage) {
        super(message, errorCode, errorMessage);
    }

    public NotFoundException(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
