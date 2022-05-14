package com.wayfair.checkout.service;

import com.wayfair.checkout.http.ProductServiceClient;
import com.wayfair.checkout.model.AddProductRequest;
import com.wayfair.checkout.model.BasketItem;
import com.wayfair.checkout.model.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CheckoutServiceIntegrationTest {
    @Autowired
    private CheckoutService service;

    @MockBean
    private ProductServiceClient productServiceClient;

    @Test
    public void addProductToBasket_validProductId_validCustomerId() {

        AddProductRequest request = new AddProductRequest().setProductId("WF0123").setQuantity(2);
        Product product = Product.builder().id("WF0123").name("Some Product").price(BigDecimal.valueOf(100.0)).build();
        Mockito.when(productServiceClient.getProductById(Mockito.anyString())).thenReturn(product);

        List<BasketItem> itemList = service.addProductToBasket(request, UUID.fromString("2e699cad-5a2d-44e4-bd4f-2bd67811390d"));

        assert  !itemList.isEmpty();
        assert itemList.get(0).getProductId().equals("WF0123");
    }

    @Test
    public void getBasketItems_validProductId_validCustomerId() {

        UUID customerId = UUID.fromString("2e699cad-5a2d-44e4-bd4f-2bd67811390d");
        AddProductRequest request1 = new AddProductRequest().setProductId("WF0123").setQuantity(2);
        AddProductRequest request2 = new AddProductRequest().setProductId("WF234").setQuantity(2);
        Product product1 = Product.builder().id("WF0123").name("Some Product").price(BigDecimal.valueOf(100.0)).build();
        Product product2 = Product.builder().id("WF234").name("Some Product").price(BigDecimal.valueOf(99.0)).build();
        Mockito.when(productServiceClient.getProductById(product1.getId())).thenReturn(product1);
        Mockito.when(productServiceClient.getProductById(product2.getId())).thenReturn(product2);

        service.addProductToBasket(request1, customerId);
        service.addProductToBasket(request2, customerId);
        List<BasketItem> items = service.getBasketItems(customerId);

        assert  !items.isEmpty();
        assert items.size() == 2;
        assert items.stream().filter(item -> item.getProductId().equals(product1.getId()) || item.getProductId().equals(product2.getId())).count() == 2;
    }

    @Test
    public void getBasketItemsPrice_validProductId_validCustomerId() {

        UUID customerId = UUID.fromString("2e699cad-5a2d-44e4-bd4f-2bd67811390d");
        AddProductRequest request1 = new AddProductRequest().setProductId("WF0123").setQuantity(2);
        AddProductRequest request2 = new AddProductRequest().setProductId("WF234").setQuantity(2);
        Product product1 = Product.builder().id("WF0123").name("Some Product").price(BigDecimal.valueOf(100.0)).build();
        Product product2 = Product.builder().id("WF234").name("Some Product").price(BigDecimal.valueOf(99.0)).build();
        Mockito.when(productServiceClient.getProductById(product1.getId())).thenReturn(product1);
        Mockito.when(productServiceClient.getProductById(product2.getId())).thenReturn(product2);

        service.addProductToBasket(request1, customerId);
        service.addProductToBasket(request2, customerId);
        BigDecimal price = service.getBasketItemsPrice(customerId);

        assert  price.equals(product1.getPrice().multiply(BigDecimal.valueOf(request1.getQuantity())).add(product2.getPrice().multiply(BigDecimal.valueOf(request2.getQuantity())) ));
    }

}
