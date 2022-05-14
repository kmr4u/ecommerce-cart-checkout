package com.wayfair.products;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(version = "V1.0.0", title = "Products Service API"))
@SpringBootApplication
public class ProductsServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(ProductsServiceMain.class, args);
    }
}
