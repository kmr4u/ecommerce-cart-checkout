package com.wayfair.checkout.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Getter
    @Value("${http.products.getById}")
    private String productsServiceUrl;

    @Getter
    @Value("${http.payments.requestPayment}")
    private String paymentsServiceUrl;

}
