package com.wayfair.payments;

import lombok.Getter;

public class PaymentException extends RuntimeException {

    @Getter
    private final ErrorCode errorCode;

    @Getter
    private final Object details;

    public PaymentException(ErrorCode errorCode, Object details, String message) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    enum ErrorCode {
        TEMPORARY_PAYMENT_FAILURE,
        BASKET_DUPLICATE
    }
}
