package com.mjc.school.service.exception;

public class AlreadyExistException extends BaseException {

    public AlreadyExistException(String message, int errorCode, String errorMessage) {
        super(message, errorCode, errorMessage);
    }

    public AlreadyExistException(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}



