package com.wayfair.checkout.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BasketItem {
    private final String productId;
    private final String productName;
    private final int quantity;
}
