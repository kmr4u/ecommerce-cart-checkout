package com.wayfair.checkout.service;

import com.wayfair.checkout.exception.ProductCheckoutException;
import com.wayfair.checkout.model.AddProductRequest;
import com.wayfair.checkout.model.BasketItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CheckoutService {
    public List<BasketItem> addProductToBasket(AddProductRequest request, UUID userId) throws ProductCheckoutException;
    public List<BasketItem> getBasketItems(UUID userId) throws ProductCheckoutException;
    public BigDecimal getBasketItemsPrice(UUID userId) throws ProductCheckoutException;
}
