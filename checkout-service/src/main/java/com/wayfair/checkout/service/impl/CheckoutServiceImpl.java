package com.wayfair.checkout.service.impl;

import com.wayfair.checkout.exception.ProductCheckoutException;
import com.wayfair.checkout.http.ProductServiceClient;
import com.wayfair.checkout.model.AddProductRequest;
import com.wayfair.checkout.model.BasketItem;
import com.wayfair.checkout.model.Product;
import com.wayfair.checkout.service.CheckoutService;
import com.wayfair.checkout.dao.CustomerBasketsDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class CheckoutServiceImpl implements CheckoutService {

    @Autowired
    private CustomerBasketsDao customerBasketsDao;

    @Autowired
    private ProductServiceClient productClient;


    @Override
    public List<BasketItem> addProductToBasket(AddProductRequest request, UUID customerId) throws ProductCheckoutException {
        log.info("Processing request to add product {} to basket by customer {}", request.getProductId(), customerId);

        Product product = productClient.getProductById(request.getProductId());

        return customerBasketsDao.addToCustomerBasket(customerId, product, request.getQuantity());

    }

    @Override
    public List<BasketItem> getBasketItems(UUID customerId) throws ProductCheckoutException{
        log.info("Processing request to retrieve basket items for customer {}", customerId);

        return customerBasketsDao.getCustomerBasketItems(customerId);
    }

    @Override
    public BigDecimal getBasketItemsPrice(UUID customerId) throws ProductCheckoutException{
        log.info("Processing request to retrieve total price basket items for customer {}", customerId);

        return customerBasketsDao.getBasketItemsPrice(customerId);
    }
}
