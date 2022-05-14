package com.wayfair.products;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class ProductServiceTest {

    private final ProductService productService = new ProductService(1234567L);
    private final Random random = new Random(123L);

    @Test
    void testUnsupportedId() {
        assertThat(productService.getProductById("123")).isEmpty();
    }

    @Test
    void testHardcodedProduct() {
        assertThat(productService.getProductById("WF0123")).contains(Product.builder()
                .id("WF0123")
                .name("Druid Corner Sofa")
                .price(new BigDecimal("549.99"))
                .build());
    }

    @Test
    void testGeneratedSampleProduct1_noOffers_priceUpTo2000() {
        random.ints(10, 0, 100000).forEach(randomId -> {
            Optional<Product> generatedProduct = productService.getProductById("WF1" + randomId);
            assertThat(generatedProduct).isNotEmpty()
                    .get()
                    .isEqualToIgnoringGivenFields(Product.builder()
                            .id("WF1" + randomId)
                            .name("Sample product WF1 1" + randomId)
                            .build(), "price");
            assertThat(generatedProduct.get().getPrice()).isBetween(BigDecimal.ZERO, new BigDecimal("2000"));
        });
    }

    @Test
    void testGeneratedSampleProduct2_2for1offer_priceUpTo1000() {
        random.ints(10, 0, 10000).forEach(randomId -> {
            Optional<Product> generatedProduct = productService.getProductById("WF2" + randomId);
            assertThat(generatedProduct).isNotEmpty()
                    .get()
                    .isEqualToIgnoringGivenFields(Product.builder()
                            .id("WF2" + randomId)
                            .name("Sample product WF2 2" + randomId)
                            .offers(singletonList("2for1"))
                            .build(), "price");
            assertThat(generatedProduct.get().getPrice()).isBetween(BigDecimal.ZERO, new BigDecimal("1000"));
        });

    }

    @Test
    void testGeneratedSampleProduct3_3for2offer_repeatedCallsSameValue() {
        random.ints(10, 0, 10000).forEach(randomVal -> {
            String randomId = "WF3" + randomVal;
            Optional<Product> generatedProduct = productService.getProductById(randomId);
            assertThat(generatedProduct).isNotEmpty()
                    .get()
                    .isEqualToIgnoringGivenFields(Product.builder()
                            .id(randomId)
                            .name("Sample product WF3 3" + randomVal)
                            .offers(singletonList("3for2"))
                            .build(), "price");
            assertThat(generatedProduct.get().getPrice()).isBetween(BigDecimal.ZERO, new BigDecimal("1000"));
            assertThat(productService.getProductById(randomId)).isEqualTo(generatedProduct);
        });

    }

    @Test
    void testNotFound() {
        random.ints(10, 0, 10000)
                .forEach(randomVal -> assertThat(productService.getProductById("WF9" + randomVal)).isEmpty());
    }

}