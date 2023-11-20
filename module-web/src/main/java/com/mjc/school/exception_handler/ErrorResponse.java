package com.mjc.school.exception_handler;

public record ErrorResponse(
        int errorCode,
        String errorMessage,
        String details
) {
}
