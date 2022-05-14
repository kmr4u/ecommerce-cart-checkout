package com.wayfair.checkout.util;

import com.wayfair.checkout.model.BasketItem;
import com.wayfair.checkout.model.Item;
import com.wayfair.checkout.model.Product;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.wayfair.checkout.model.Product.Promotion.*;

public class CheckoutServiceUtil {

    public static List<BasketItem> transformIntoBasketItemsList(Collection<Item> items) {
      return items
                .stream()
                .map(item -> BasketItem.builder()
                               .productId(item.getProduct().getId())
                               .productName(item.getProduct().getName())
                               .quantity(item.getQuantity())
                               .build())
                .collect(Collectors.toList());
    }

    public static BigDecimal applyPromotion(Item item) {
        if(isOfferApplicable(item.getProduct(), TWO_FOR_ONE)) {
            if(item.getQuantity() >= 2) return item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity() - item.getQuantity()/2 ));
        }
        else if(isOfferApplicable(item.getProduct(), THREE_FOR_TWO)) {
            if(item.getQuantity() >= 3) return item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()- item.getQuantity()/3));
        }
        return item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

    }

    private static boolean isOfferApplicable(Product product, Product.Promotion promotion) {
        return !CollectionUtils.isEmpty(product.getOffers()) && product.getOffers().contains(promotion.getCode());
    }
}
