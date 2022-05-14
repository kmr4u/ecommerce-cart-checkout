package com.wayfair.products;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.singletonList;

@Service
public class ProductService {

    //hardcoding a handful of products and the rest will be generated randomly on the fly
    public static final Product SOFA = Product.builder()
            .id("WF0123")
            .name("Druid Corner Sofa")
            .price(new BigDecimal("549.99"))
            .build();
    public static final Product DINING_TABLE = Product.builder()
            .id("WF01234")
            .name("Trapp Dining Table")
            .price(new BigDecimal("149.99"))
            .offers(singletonList("2for1"))
            .build();
    public static final Product LAMP = Product.builder()
            .id("WF012345")
            .name("Mishler Standing Lamp")
            .price(new BigDecimal("36.49"))
            .offers(singletonList("3for2"))
            .build();

    private final Map<String, Product> products = new ConcurrentHashMap<>();
    private final Random random;

    public ProductService() {
        this(System.currentTimeMillis());
    }

    protected ProductService(long randomSeed) {
        this.random = new Random(randomSeed);
        this.products.put(SOFA.getId(), SOFA);
        this.products.put(DINING_TABLE.getId(), DINING_TABLE);
        this.products.put(LAMP.getId(), LAMP);
    }

    public Optional<Product> getProductById(String productId) {
        if (!productId.startsWith("WF")) {
            return Optional.empty();
        }

        return Optional.ofNullable(products.computeIfAbsent(productId, this::sampleProductForId));

    }

    private Product sampleProductForId(String productId) {
        if (productId.startsWith("WF1")) {
            return Product.builder()
                    .id(productId)
                    .name("Sample product WF1 " + productId.substring(2))
                    .price(BigDecimal.valueOf(random.nextInt(20000) / 10.0))
                    .build();
        }

        if (productId.startsWith("WF2")) {
            return Product.builder()
                    .id(productId)
                    .name("Sample product WF2 " + productId.substring(2))
                    .price(BigDecimal.valueOf(random.nextInt(10000) / 10.0))
                    .offers(singletonList("2for1"))
                    .build();
        }
        if (productId.startsWith("WF3")) {
            return Product.builder()
                    .id(productId)
                    .name("Sample product WF3 " + productId.substring(2))
                    .price(BigDecimal.valueOf(random.nextInt(1000) / 10.0))
                    .offers(singletonList("3for2"))
                    .build();
        }

        return null;
    }

}
