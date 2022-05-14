package com.wayfair.products;

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

}
