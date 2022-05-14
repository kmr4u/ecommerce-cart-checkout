package com.wayfair.payments;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.constraints.NotNull;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ServiceError> handlePaymentException(PaymentException exception) {
        log.error("Failed payment", exception);
        ServiceError serviceError = ServiceError.from(exception);
        switch (exception.getErrorCode()) {
            case BASKET_DUPLICATE:
                return ResponseEntity.status(BAD_REQUEST).body(serviceError);
            case TEMPORARY_PAYMENT_FAILURE:
                return ResponseEntity.status(SERVICE_UNAVAILABLE).body(serviceError);
            default:
                return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(serviceError);
        }
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleExceptionInternal(ex, ServiceError.from(ex), headers, status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServiceError> handleUnexpected(Exception exception) {
        log.error("Unexpected exception", exception);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ServiceError(exception.getMessage(), null, null));
    }

    @Data
    public static class ServiceError {
        @NotNull
        private final String errorMessage;
        private final String errorCode;
        private final Object details;

        public static ServiceError from(PaymentException paymentException) {
            return new ServiceError(paymentException.getMessage(), paymentException.getErrorCode().name(), paymentException.getDetails());
        }

        public static ServiceError from(MethodArgumentNotValidException ex) {
            return new ServiceError("Validation failed", "VALIDATION_ERROR", ex.getBindingResult().getAllErrors());
        }
    }
}
