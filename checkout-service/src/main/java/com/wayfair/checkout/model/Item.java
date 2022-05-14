package com.wayfair.checkout.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Item {
    private final Product product;
    private final int quantity;
}
