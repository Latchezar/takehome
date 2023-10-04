package com.example.takehome.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerExceptionHandler {

    @ExceptionHandler( {SQLException.class, DataAccessException.class})
    public ResponseEntity<ErrorDetails> databaseError(HttpServletRequest req,
                                                      Exception exception) {
        logException(req.getRequestURI(), exception.getClass().getSimpleName(), exception);
        ErrorDetails errorDetails = new ErrorDetails(ErrorCode.DATABASE_ERROR.getCode(),
                                                     exception.getLocalizedMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ServiceException.class)
    public ResponseEntity<ErrorDetails> serviceFailure(HttpServletRequest req,
                                                       ServiceException e) {
        logException(req.getRequestURI(), e.getClass().getSimpleName(), e);
        ErrorDetails errorDetails = getErrorDetails(e);
        return ResponseEntity.status(e.getError().getStatus().value()).body(errorDetails);
    }

    @ExceptionHandler( {MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorDetails> javaBeanValidationError(HttpServletRequest req,
                                                                Exception exception) {
        logException(req.getRequestURI(), exception.getClass().getSimpleName(), exception);
        ErrorDetails errorDetails = ErrorDetails.builder()
                                                .errorCode(HttpStatus.BAD_REQUEST.value())
                                                .description(exception.getMessage())
                                                .build();
        return ResponseEntity.badRequest().body(errorDetails);
    }

    @ExceptionHandler( {RuntimeException.class})
    public ResponseEntity<ErrorDetails> runtimeError(HttpServletRequest req,
                                                     Exception exception) {
        logException(req.getRequestURI(), exception.getClass().getSimpleName(), exception);
        ErrorDetails errorDetails = ErrorDetails.builder()
                                                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                                .description(exception.getMessage())
                                                .build();
        return ResponseEntity.internalServerError().body(errorDetails);
    }

    @ExceptionHandler( {AccessDeniedException.class})
    public ResponseEntity<ErrorDetails> handleAccessDeniedException(HttpServletRequest req,
                                                                    Exception exception) {
        logException(req.getRequestURI(), exception.getClass().getSimpleName(), exception);
        return new ResponseEntity<>(new ErrorDetails(10403, "Access denied"), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler( {IllegalArgumentException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorDetails> handleIllegalArgumentException(HttpServletRequest req,
                                                                       Exception exception) {
        logException(req.getRequestURI(), exception.getClass().getSimpleName(),
                     exception);
        return new ResponseEntity<>(new ErrorDetails(10400, "Bad request: " + exception.getMessage()),
                                    HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( {BindException.class})
    protected ResponseEntity<Object> handleMethodArgumentNotValid(HttpServletRequest req,
                                                                  BindException exception) {
        logException(req.getRequestURI(), exception.getClass().getSimpleName(), exception);

        List<String> errorMessage = exception
                .getBindingResult()
                .getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler( {ConstraintViolationException.class})
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
        String error = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("."));

        return new ResponseEntity<>(new ErrorDetails(HttpStatus.BAD_REQUEST.value(), error), HttpStatus.BAD_REQUEST);
    }

    private void logException(String requestURI,
                              String simpleName,
                              Exception exception) {
        log.error("Request '{}' raised {}. {}", requestURI, simpleName, exception.getMessage());
    }

    private ErrorDetails getErrorDetails(ServiceException e) {
        return new ErrorDetails(e.getError().getCode(), e.getError().getDescription());
    }

}
