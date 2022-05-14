package com.wayfair.checkout.model;


import lombok.Getter;
import lombok.Setter;

import java.util.*;

public class Basket {
    @Getter
    @Setter
    private UUID customerId;
    private final Collection<Item> items = Collections.synchronizedList(new ArrayList<Item>());

    public Collection<Item> getItems() {
       return this.items;
    }

    public void addItem(Product product, int quantity) {
        if(isExistingProductWithSameQuantity(product.getId(), quantity)) return;
        else if(getItems().removeIf(item -> item.getProduct().getId().equals(product.getId()) && item.getQuantity() != quantity)) {
            getItems().add(Item.builder().product(product).quantity(quantity).build());
            return;
        }
        getItems().add(Item.builder().product(product).quantity(quantity).build());
    }

    public void removeItem(Item item) {
        getItems().remove(item);
    }

    public boolean isExistingProductWithSameQuantity(String productId, int quantity) {
        synchronized (this.items) {
            Iterator<Item> iterator = this.items.iterator();
            while (iterator.hasNext()) {
                Item item = iterator.next();
                if(item.getProduct().getId().equals(productId) && item.getQuantity() == quantity) return true;
            }
            return false;
        }
    }
}
