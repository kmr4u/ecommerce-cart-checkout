package com.wayfair.checkout.model;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class AddProductRequest {
    @NotNull
    private String productId;
    @NotNull
    @Min(1)
    private int quantity;
}
