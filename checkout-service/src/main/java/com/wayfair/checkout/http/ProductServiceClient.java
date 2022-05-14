package com.wayfair.checkout.http;

import com.wayfair.checkout.config.ApplicationConfiguration;
import com.wayfair.checkout.exception.ProductCheckoutException;
import com.wayfair.checkout.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

import static com.wayfair.checkout.exception.ProductCheckoutException.ErrorCode.PRODUCT_ID_NOT_FOUND;
import static com.wayfair.checkout.exception.ProductCheckoutException.ErrorCode.PRODUCT_SERVICE_ERROR;

@Slf4j
@Component
public class ProductServiceClient {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApplicationConfiguration config;

    public Product getProductById(String productId) throws ProductCheckoutException {
        log.info("get product by id {}", productId);
        try {
            ResponseEntity<Product> responseEntity = restTemplate.getForEntity(UriComponentsBuilder.fromUriString(config.getProductsServiceUrl()).build(Map.of("id", productId)), Product.class);
            if(responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            }
        }catch (HttpStatusCodeException e) {
            log.error("Error while getting product  {} details", productId);
            if(e.getStatusCode().is4xxClientError()){
                throw new ProductCheckoutException(PRODUCT_ID_NOT_FOUND, "Please use a valid product id", "Product ID Not Found");
            }else if(e.getStatusCode().is5xxServerError()) {
                throw new ProductCheckoutException(PRODUCT_SERVICE_ERROR, "Could not reach product service", "Product Service Unavailable");
            }
            throw new ProductCheckoutException(PRODUCT_ID_NOT_FOUND, "Please use a valid product id", "Product ID Not Found");
        }
        catch (Exception e) {
            log.error("Error while getting product  {} details", productId);
            throw new ProductCheckoutException(PRODUCT_SERVICE_ERROR, "Could not reach product service", e.getCause().toString());
        }

        return null;
    }
}
