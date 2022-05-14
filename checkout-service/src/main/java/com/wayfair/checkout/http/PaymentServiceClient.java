package com.wayfair.checkout.http;

import com.wayfair.checkout.exception.ProductCheckoutException;
import com.wayfair.checkout.model.AddProductRequest;
import com.wayfair.checkout.model.BasketItem;
import com.wayfair.checkout.model.PaymentRequest;
import com.wayfair.checkout.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.wayfair.checkout.exception.ProductCheckoutException.ErrorCode.*;

@Component
public class PaymentServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    public String submitPayment(PaymentRequest req) {

        try {
            HttpEntity<PaymentRequest> entity = new HttpEntity<>(req, null);

            ResponseEntity<String> responseEntity = restTemplate.exchange("http://localhost:8082/api/v1/payments", HttpMethod.POST, entity, String.class);
            if(responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            }
        }catch (HttpStatusCodeException e) {
            if(e.getStatusCode().is4xxClientError()){
                throw new ProductCheckoutException(PAYMENT_SERVICE_ERROR, "Payment failed", "Please try again");
            }else if(e.getStatusCode().is5xxServerError()) {
                throw new ProductCheckoutException(PAYMENT_SERVICE_ERROR, "Payment failed", "Please try again");
            }
            throw new ProductCheckoutException(PAYMENT_SERVICE_ERROR, "Payment failed", "Please try again");
        }

        return null;
    }
}
