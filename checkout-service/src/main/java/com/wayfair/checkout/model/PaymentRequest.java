package com.wayfair.checkout.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private String orderId;
    private BigDecimal price;
}
