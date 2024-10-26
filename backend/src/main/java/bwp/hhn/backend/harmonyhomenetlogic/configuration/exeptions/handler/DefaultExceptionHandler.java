package bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.handler;

import bwp.hhn.backend.harmonyhomenetlogic.configuration.exeptions.customErrors.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.time.LocalDateTime;

@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler({
            NullPointerException.class, IllegalStateException.class,
            IndexOutOfBoundsException.class, UnsupportedOperationException.class,
            UserNotFoundException.class, ApartmentNotFoundException.class,
            PaymentNotFoundException.class, PaymentComponentNotFoundException.class,
            PollNotFoundException.class, PossessionHistoryNotFoundException.class,
            VoteNotFoundException.class, TopicNotFoundException.class,
            ProblemReportNotFoundException.class, NotificationNotFoundException.class
    })
    public ResponseEntity<ApiError> handleNotFoundExceptions(Exception e, HttpServletRequest request) {
        return createResponseEntity(e, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            IllegalArgumentException.class, RuntimeException.class,
            IOException.class, NumberFormatException.class
    })
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        return createResponseEntity(e, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(DataIntegrityViolationException e, HttpServletRequest request) {
        return createResponseEntity(e, request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e, HttpServletRequest request) {
        return createResponseEntity(e, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiError> createResponseEntity(Exception e, HttpServletRequest request, HttpStatus status) {
        ApiError apiError = new ApiError(request.getRequestURI(), e.getMessage(), status.value(), LocalDateTime.now());
        return new ResponseEntity<>(apiError, status);
    }

}
