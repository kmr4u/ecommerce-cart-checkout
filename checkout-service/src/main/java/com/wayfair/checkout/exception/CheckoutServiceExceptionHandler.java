package com.wayfair.checkout.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.constraints.NotNull;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class CheckoutServiceExceptionHandler {

    @ExceptionHandler({ProductCheckoutException.class})
    public final ResponseEntity<ServiceError> handleProductCheckoutException(ProductCheckoutException ex) {
        ServiceError serviceError = ServiceError.from(ex);
        switch (ex.getErrorCode()) {
            case PRODUCT_ID_NOT_FOUND:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(serviceError);
            case PRODUCT_SERVICE_ERROR:
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(serviceError);
            case CUSTOMER_ID_NOT_FOUND:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(serviceError);
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(serviceError);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServiceError> handleUnexpected(Exception exception) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ServiceError(exception.getMessage(), null, null));
    }

    @Data
    public static class ServiceError {
        @NotNull
        private final String errorMessage;
        private final String errorCode;
        private final Object details;

        public static ServiceError from(ProductCheckoutException productCheckoutException) {
            return new ServiceError(productCheckoutException.getMessage(), productCheckoutException.getErrorCode().name(), productCheckoutException.getDetails());
        }
    }
}
