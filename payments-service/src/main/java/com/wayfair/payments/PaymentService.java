package com.wayfair.payments;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.wayfair.payments.PaymentException.ErrorCode.BASKET_DUPLICATE;
import static com.wayfair.payments.PaymentException.ErrorCode.TEMPORARY_PAYMENT_FAILURE;

@Slf4j
@Service
public class PaymentService {

    private final Map<String, Payment> payments = new ConcurrentHashMap<>();

    /**
     * Accepts payment request for given basket/value and returns payment id
     *
     * @return paymentId (UUID)
     * @throws PaymentException when multiple payments for same basket sent
     * @throws PaymentException when basketId starts with eee
     */
    public String requestPayment(String basketId, BigDecimal paymentValue)
            throws IllegalArgumentException, PaymentException {
        if (basketId.startsWith("eee")) {
            throw new PaymentException(TEMPORARY_PAYMENT_FAILURE, Map.of("basketId", basketId),
                    "Failed to process payment request for basket, please try again later");
        }

        Payment payment = Payment.builder()
                .basketId(basketId)
                .paymentValue(paymentValue)
                .id(UUID.randomUUID().toString())
                .build();
        Payment previousPayment = payments.putIfAbsent(payment.getBasketId(), payment);
        if (previousPayment != null) {
            throw new PaymentException(BASKET_DUPLICATE, Map.of("basketId", basketId, "previousPaymentId", previousPayment.getId()),
                    "Multiple payments for same basket id are not allowed");
        }

        return payment.getId();
    }

    @Data
    @Builder
    public static class Payment {
        private final String basketId;
        private final String id;
        private final BigDecimal paymentValue;
    }
}
