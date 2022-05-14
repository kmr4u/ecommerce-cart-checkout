package com.wayfair.checkout.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Customer {
    private UUID customerId;
    private String firstName;
    private String lastName;
    private String email;
    private Basket basket;

}
