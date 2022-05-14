package com.wayfair.checkout.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wayfair.checkout.exception.ProductCheckoutException;
import com.wayfair.checkout.model.*;
import com.wayfair.checkout.util.CheckoutServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Component
public class CustomerBasketsDao implements CommandLineRunner {

    private static final Map<UUID, Basket> customerBaskets = new HashMap<>();

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void run(String... args) throws Exception {

        URL resource = getClass().getClassLoader().getResource("customers.json");
        List<Customer> customers = mapper.readValue(Paths.get(resource.toURI()).toFile(), new TypeReference<List<Customer>>(){});

        customers.forEach( customer -> customer.setBasket(new Basket().setCustomerId(customer.getCustomerId())));
        customers.forEach(customer -> customerBaskets.put(customer.getCustomerId(), customer.getBasket()));
    }

    public List<BasketItem> addToCustomerBasket(UUID customerId, Product product, int quantity) throws ProductCheckoutException {

        validateCustomerId(customerId);

        customerBaskets.get(customerId).addItem(product, quantity);

        log.info("product added to basket");
        return getCustomerBasketItems(customerId);
    }

    public List<BasketItem> getCustomerBasketItems(UUID customerId) throws ProductCheckoutException {

        validateCustomerId(customerId);

        Collection<Item> items = customerBaskets.get(customerId).getItems();
        synchronized (items) {
            return CheckoutServiceUtil.transformIntoBasketItemsList(items);
        }

    }

    public BigDecimal getBasketItemsPrice(UUID customerId) throws ProductCheckoutException {
        Collection<Item> items = customerBaskets.get(customerId).getItems();
        synchronized (items) {
            return items.stream().map(item -> CheckoutServiceUtil.applyPromotion(item)).reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    private void validateCustomerId(UUID customerId) throws ProductCheckoutException {

        if(!customerBaskets.containsKey(customerId)) {
            log.error("invalid customer id");
            throw new ProductCheckoutException(ProductCheckoutException.ErrorCode.CUSTOMER_ID_NOT_FOUND, "Please use a valid customer id", "Customer ID Not Found");
        }
    }
}
