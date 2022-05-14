package com.wayfair.payments;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaymentServiceTest {

    private final PaymentService paymentService = new PaymentService();

    @Test
    void testPaymentSuccess_returnsUUID() {
        assertThat(paymentService.requestPayment("b1", new BigDecimal("123.11"))).matches("[a-f0-9]{8}-([a-f0-9]{4}-){3}[a-f0-9]{12}");
    }

    @Test
    void testPaymentForErrorBasket_failsWithPaymentException() {
        assertThatThrownBy(() -> paymentService.requestPayment("eee-123", new BigDecimal("10000"))).isInstanceOf(PaymentException.class)
                .hasMessage("Failed to process payment request for basket, please try again later")
                .hasFieldOrPropertyWithValue("details", Map.of("basketId", "eee-123"))
                .hasFieldOrPropertyWithValue("errorCode", PaymentException.ErrorCode.TEMPORARY_PAYMENT_FAILURE);
    }

    @Test
    void testMultiplePaymentsForSameBasket_fails() {
        String paymentId = paymentService.requestPayment("b1", new BigDecimal("123"));
        assertThatThrownBy(() -> paymentService.requestPayment("b1", new BigDecimal("456"))).isInstanceOf(PaymentException.class)
                .hasMessage("Multiple payments for same basket id are not allowed")
                .hasFieldOrPropertyWithValue("details", Map.of("basketId", "b1", "previousPaymentId", paymentId))
                .hasFieldOrPropertyWithValue("errorCode", PaymentException.ErrorCode.BASKET_DUPLICATE);
    }

}