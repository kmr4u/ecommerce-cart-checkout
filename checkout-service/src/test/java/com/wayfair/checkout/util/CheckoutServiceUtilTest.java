package com.wayfair.checkout.util;

import com.wayfair.checkout.model.BasketItem;
import com.wayfair.checkout.model.Item;
import com.wayfair.checkout.model.Product;
import com.wayfair.checkout.service.CheckoutService;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@SpringBootTest
public class CheckoutServiceUtilTest {

    private final Product product1 = Product.builder().id("123").name("some product").price(BigDecimal.valueOf(100.0)).offers(Collections.singletonList("3for2")).build();
    private final Product product2 = Product.builder().id("456").name("some other product").price(BigDecimal.valueOf(199.0)).offers(Collections.singletonList("2for1")).build();
    private final List<Item> items = List.of(Item.builder().product(product1).quantity(6).build(), Item.builder().product(product2).quantity(4).build());

    @Test
    public void test_transform_items_into_basketItems() {

        List<BasketItem> basketItems = CheckoutServiceUtil.transformIntoBasketItemsList(items);

        assert basketItems.size() == 2  && basketItems.stream().filter(item -> item.getProductId().equals(product1.getId()) || item.getProductId().equals(product2.getId())).count() == 2;
    }

    @Test
    public void test_applyPromotion() {
        BigDecimal total = items.stream().map(item -> CheckoutServiceUtil.applyPromotion(item)).reduce(BigDecimal.ZERO, BigDecimal::add);

        assert total.equals(product1.getPrice().multiply(BigDecimal.valueOf(4)).add(product2.getPrice().multiply(BigDecimal.valueOf(2))));
    }
}
