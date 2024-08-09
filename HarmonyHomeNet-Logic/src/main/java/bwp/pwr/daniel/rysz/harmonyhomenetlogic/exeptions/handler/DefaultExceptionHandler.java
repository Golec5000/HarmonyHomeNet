package bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.handler;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.BuildingNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.DocumentNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.time.LocalDateTime;

@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler({
            NullPointerException.class, NoResourceFoundException.class,
            BuildingNotFoundException.class, UserNotFoundException.class,
            DocumentNotFoundException.class
    })
    public ResponseEntity<ApiError> handleNotFoundExceptions(Exception e, HttpServletRequest request) {
        return createResponseEntity(e, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            IllegalArgumentException.class, RuntimeException.class,
            IOException.class
    })
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        return createResponseEntity(e, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(DataIntegrityViolationException e, HttpServletRequest request) {
        return createResponseEntity(e, request, HttpStatus.CONFLICT);
    }

    private ResponseEntity<ApiError> createResponseEntity(Exception e, HttpServletRequest request, HttpStatus status) {
        ApiError apiError = new ApiError(request.getRequestURI(), e.getMessage(), status.value(), LocalDateTime.now());
        return new ResponseEntity<>(apiError, status);
    }
}