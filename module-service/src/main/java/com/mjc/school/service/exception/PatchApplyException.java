package com.mjc.school.service.exception;

public class PatchApplyException extends BaseException {

    public PatchApplyException(String message, int errorCode, String errorMessage) {
        super(message, errorCode, errorMessage);
    }

    public PatchApplyException(int errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
