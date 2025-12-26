package com.yearis.e_commerce.exception;

import com.yearis.e_commerce.payload.error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ProductNotFoundException.class,
            CartNotFoundException.class,
            OrderNotFoundException.class,
            CategoryNotFoundException.class,
            CartItemNotFoundException.class,
            RoleNotFoundException.class,     // Added from your list
            UsernameNotFoundException.class  // Built-in Spring Security exception
    })
    public ResponseEntity<ErrorResponse> handleResourceNotFound(RuntimeException ex) {

        return buildResponse(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage());
    }

    @ExceptionHandler({
            InventoryException.class,
            ActionNotAllowedException.class,
            InvalidPasswordException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex) {

        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid Action", ex.getMessage());
    }

    @ExceptionHandler(ResourceAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(RuntimeException ex) {

        return buildResponse(HttpStatus.FORBIDDEN, "Access Denied", ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException ex) {

        return buildResponse(HttpStatus.CONFLICT, "Resource Conflict", ex.getMessage());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleConcurrencyFailure(ObjectOptimisticLockingFailureException ex) {

        return buildResponse(HttpStatus.CONFLICT, "Concurrent Update Conflict",
                "The item price or availability changed while you were checking out. Please refresh and try again.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An unexpected error occurred. Please try again later.");
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String error, String message) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                error,
                message
        );
        return new ResponseEntity<>(response, status);
    }
}
