# Shopping Cart Checkout System

The application implements the following use cases

As a user, I would like to:
* Be able to add products to a basket by product ID.
* Be able to see a list of product IDs in my basket.
* Be able to get a list of all the products in my basket.
* Be able to get the total value of the basket.
* Take any valid promotions into account when calculating the total value of my basket.

# Prerequisites
* Java 11
* product-service is running on port 8081
* payment-service is running on port 8082

# product service
Information about product service can be found [here](docs/product-service.md)

# payment service
Information about payment service can be found [here](docs/payment-service.md)

# Checkout Service

The application is developed using Java 11, SpringBoot and built using Gradle.

The application can be launched locally by running `com.wayfair.checkout.CheckoutServiceApplication`. It will run on port 8080 by default. 

You can also run this via docker with the following commands:

```bash
docker-compose build checkout
docker-compose up checkout
```

All three services can also be launched together by running the following command:

```bash
docker-compose up
```

Swagger documentation for the service can be found at: [/swagger endpoint](http://localhost:8080/swagger)

Postman collection for the service end points can be found [here](docs/checkout-service.postman_collection.json)

# Implementation
The service uses in-memory data structure for storing customers and their baskets information.

The customers are statically loaded into memory during the start of the application from [customers data](src/main/resources/customers.json) file and an empty basket is assigned to each one of them. 
The customers ids are represented in UUID format. For the sake of this implementation, our customers are lead roles from a few popular TV series.

For product information, it depends on the product-service and expects that product-service is running on port 8081.

Only the products available in product inventory can be added by only the customers available in customer data file.

For this MVP, there is at most one entry per product in the customer's basket. 
If there are multiple requests to add the same productId (same or different quantity) by the same customer, the product quantity in the latest request served by the server gets written to the basket. 
Of course this can be extended to increment/decrement quantity of a product in customer's basket.