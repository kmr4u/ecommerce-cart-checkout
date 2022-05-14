package com.wayfair.payments;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(version = "V1.0.0", title = "Payments Service API"))
@SpringBootApplication
public class PaymentsServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(PaymentsServiceMain.class, args);
    }
}
