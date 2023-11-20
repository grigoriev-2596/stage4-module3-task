package com.mjc.school.exception_handler;

import com.mjc.school.service.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.security.access.AccessDeniedException;
import javax.validation.ConstraintViolationException;

import static com.mjc.school.service.exception.ErrorCode.ACCESS_DENIED;

@RestControllerAdvice
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = NotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(NotFoundException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage(), ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(ErrorCode.ENTITY_VALIDATION_FAILED.getId(),
                ErrorCode.ENTITY_VALIDATION_FAILED.getMessage(), ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = PatchApplyException.class)
    protected ResponseEntity<Object> handlePatchApplyException(PatchApplyException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage(), ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = AlreadyExistException.class)
    protected ResponseEntity<Object> handleAlreadyExistException(AlreadyExistException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage(), ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = JwtAuthenticationException.class)
    protected ResponseEntity<Object> handleJwtException(JwtAuthenticationException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(ex.getErrorCode(), ex.getErrorMessage(), ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(ACCESS_DENIED.getId(), ACCESS_DENIED.getMessage(), ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<Object> handleUnexpectedExceptions(RuntimeException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(ErrorCode.UNEXPECTED_SERVER_PROBLEM.getId(),
                ErrorCode.UNEXPECTED_SERVER_PROBLEM.getMessage(), ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
