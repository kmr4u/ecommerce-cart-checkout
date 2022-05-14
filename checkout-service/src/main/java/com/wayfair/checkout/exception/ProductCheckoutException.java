package com.wayfair.checkout.exception;

import lombok.Getter;

public class ProductCheckoutException extends RuntimeException {

    @Getter
    private final ErrorCode errorCode;

    @Getter
    private final Object details;

    public ProductCheckoutException(ErrorCode errorCode, Object details, String message) {
        super(message);
        this.details = details;
        this.errorCode = errorCode;
    }

    public enum ErrorCode {
        CUSTOMER_ID_NOT_FOUND,
        PRODUCT_ID_NOT_FOUND,
        PRODUCT_SERVICE_ERROR,
        PAYMENT_SERVICE_ERROR
    }
}
