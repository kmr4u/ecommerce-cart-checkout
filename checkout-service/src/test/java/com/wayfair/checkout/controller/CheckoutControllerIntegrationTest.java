package com.wayfair.checkout.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wayfair.checkout.CheckoutServiceApplication;
import com.wayfair.checkout.model.AddProductRequest;
import com.wayfair.checkout.model.BasketItem;
import com.wayfair.checkout.model.Product;
import com.wayfair.checkout.service.CheckoutService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CheckoutServiceApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CheckoutControllerIntegrationTest {

    private final static String URL_GET_PRODUCT_BY_ID = "http://localhost:8081/api/v1/products/";
    private final static String URL_CHECKOUT_ITEMS = "/api/v1/basket/items";
    private final static String URL_BASKET_ITEMS_PRICE = "/api/v1/basket/items/total";
    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CheckoutService checkoutService;

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void init(){
        mockServer = MockRestServiceServer.createServer(this.restTemplate);
    }

    HttpHeaders headers = new HttpHeaders();

    @Test
    public void addProductToBasket_validCustomerId_validProductId() throws JsonProcessingException {
        AddProductRequest req = new AddProductRequest().setProductId("WF012345").setQuantity(2);

        Product product = Product.builder()
                                 .id("WF012345")
                                 .name("Mishler Standing Lamp")
                                 .price(new BigDecimal("36.49"))
                                 .offers(singletonList("3for2"))
                                 .build();

        mockProductServiceCall(req.getProductId(), product);

        headers.add("customerId", "2e699cad-5a2d-44e4-bd4f-2bd67811390d");
        HttpEntity<AddProductRequest> entity = new HttpEntity<>(req, headers);

        ResponseEntity<List<BasketItem>> responseEntity = testRestTemplate.exchange(createURLWithPort(URL_CHECKOUT_ITEMS), HttpMethod.POST, entity, new ParameterizedTypeReference<List<BasketItem>>() {});

        assert responseEntity.getStatusCode().is2xxSuccessful();
        assert !responseEntity.getBody().isEmpty();
    }

    @Test
    public void getBasketItems_validCustomerId_validProductId() throws JsonProcessingException {
        AddProductRequest req = new AddProductRequest().setProductId("WF012345").setQuantity(2);

        Product product = expectedProduct("WF012345", "Mishler Standing Lamp", new BigDecimal("36.49"), singletonList("3for2"));

        mockProductServiceCall(req.getProductId(), product);

        checkoutService.addProductToBasket(req, UUID.fromString("2e699cad-5a2d-44e4-bd4f-2bd67811390d"));

        headers.add("customerId", "2e699cad-5a2d-44e4-bd4f-2bd67811390d");
        HttpEntity<AddProductRequest> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List<BasketItem>> responseEntity = testRestTemplate.exchange(createURLWithPort(URL_CHECKOUT_ITEMS), HttpMethod.GET, entity, new ParameterizedTypeReference<List<BasketItem>>() {});

        assert responseEntity.getStatusCode().is2xxSuccessful();
        assert !responseEntity.getBody().isEmpty();

    }

    @Test
    public void getBasketItemsPrice_validCustomerId_validProductId() throws JsonProcessingException {
        AddProductRequest req = new AddProductRequest().setProductId("WF012345").setQuantity(3);

        Product product = expectedProduct("WF012345", "Mishler Standing Lamp", new BigDecimal("36.49"), singletonList("3for2"));

        mockProductServiceCall(req.getProductId(), product);

        checkoutService.addProductToBasket(req, UUID.fromString("2e699cad-5a2d-44e4-bd4f-2bd67811390d"));

        headers.add("customerId", "2e699cad-5a2d-44e4-bd4f-2bd67811390d");
        HttpEntity<AddProductRequest> entity = new HttpEntity<>(null, headers);

        ResponseEntity<BigDecimal> responseEntity = testRestTemplate.exchange(createURLWithPort(URL_BASKET_ITEMS_PRICE), HttpMethod.GET, entity, BigDecimal.class);

        assert responseEntity.getStatusCode().is2xxSuccessful();
        assert responseEntity.getBody().equals( product.getPrice().multiply(BigDecimal.valueOf(2)) );


    }

    @Test
    public void error_addProductToBasket_InValidCustomerId_validProductId() throws JsonProcessingException {
        AddProductRequest req = new AddProductRequest().setProductId("WF012345").setQuantity(2);

        Product product = Product.builder()
                .id("WF012345")
                .name("Mishler Standing Lamp")
                .price(new BigDecimal("36.49"))
                .offers(singletonList("3for2"))
                .build();

        mockProductServiceCall(req.getProductId(), product);

        headers.add("customerId", "2b799cad-5a2d-44e4-bd4f-2be67811290d");
        HttpEntity<AddProductRequest> entity = new HttpEntity<>(req, headers);

        ResponseEntity<Object> responseEntity = testRestTemplate.exchange(createURLWithPort(URL_CHECKOUT_ITEMS), HttpMethod.POST, entity, Object.class);

        assert responseEntity.getStatusCode().is4xxClientError();
        assert responseEntity.getBody().toString().contains("Customer ID Not Found");

    }

    @Test
    public void error_addProductToBasket_ValidCustomerId_InValidProductId() throws JsonProcessingException {
        AddProductRequest req = new AddProductRequest().setProductId("WF112346").setQuantity(2);

        mockServer.expect(ExpectedCount.once(), requestTo(URI.create("http://localhost:8081/api/v1/products/"+req.getProductId())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("Product Id not found")
                );

        headers.add("customerId", "2e699cad-5a2d-44e4-bd4f-2bd67811390d");
        HttpEntity<AddProductRequest> entity = new HttpEntity<>(req, headers);

        ResponseEntity<Object> responseEntity = testRestTemplate.exchange(createURLWithPort(URL_CHECKOUT_ITEMS), HttpMethod.POST, entity, Object.class);

        assert responseEntity.getStatusCode().is4xxClientError();
        assert responseEntity.getBody().toString().contains("Product ID Not Found");

    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    private void mockProductServiceCall(String productId, Product product) throws JsonProcessingException {
        mockServer.expect(ExpectedCount.once(), requestTo(URI.create(URL_GET_PRODUCT_BY_ID+productId)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(product))
                );
    }

    private Product expectedProduct(String productId, String productName, BigDecimal price, List<String> offers) {
        return Product.builder()
                .id(productId)
                .name(productName)
                .price(price)
                .offers(offers)
                .build();
    }
}
