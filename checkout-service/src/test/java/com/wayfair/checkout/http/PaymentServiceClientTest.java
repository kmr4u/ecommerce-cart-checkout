package com.wayfair.checkout.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wayfair.checkout.exception.ProductCheckoutException;
import com.wayfair.checkout.model.AddProductRequest;
import com.wayfair.checkout.model.BasketItem;
import com.wayfair.checkout.model.PaymentRequest;
import com.wayfair.checkout.model.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.wayfair.checkout.exception.ProductCheckoutException.ErrorCode.PRODUCT_ID_NOT_FOUND;
import static com.wayfair.checkout.exception.ProductCheckoutException.ErrorCode.PRODUCT_SERVICE_ERROR;
import static java.util.Collections.singletonList;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
public class PaymentServiceClientTest {

    @Mock
    private TestRestTemplate restTemplate;

    @InjectMocks
    private PaymentServiceClient paymentServiceClient;

    @LocalServerPort
    private int port;

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();
    @Before
    public void init(){
        mockServer = MockRestServiceServer.createServer(this.restTemplate);
    }

    @Test
    public void test_return_paymetId_from_paymentService() throws JsonProcessingException {

        PaymentRequest req = new PaymentRequest().setOrderId("WF012345").setPrice(BigDecimal.valueOf(200.0));


        mockPaymentServiceCall(req);

        HttpEntity<PaymentRequest> entity = new HttpEntity<>(req, null);

        ResponseEntity<List<BasketItem>> responseEntity = restTemplate.exchange(createURLWithPort("/api/v1/payments"), HttpMethod.POST, entity, new ParameterizedTypeReference<List<BasketItem>>() {});

        assert responseEntity.getStatusCode().is2xxSuccessful();
        assert !responseEntity.getBody().isEmpty();

    }

    private void mockPaymentServiceCall(PaymentRequest req) throws JsonProcessingException {
        String paymentId = "12345";
        mockServer.expect(ExpectedCount.once(), requestTo(URI.create("/api/v1/payments")))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(paymentId))
                );
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}

