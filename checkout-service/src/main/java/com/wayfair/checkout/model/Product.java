package com.wayfair.checkout.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class Product {

    private final String id;
    private final String name;

    private final BigDecimal price;
    private final List<String> offers;

    public enum Promotion {
        TWO_FOR_ONE("2for1"),
        THREE_FOR_TWO("3for2");

        private final String code;

        private Promotion(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }
    }
}
