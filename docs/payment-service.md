### Payment Service

You can launch this service locally by running `com.wayfair.payments.PaymentsServiceMain` from payments-service. It will run on port 8082 by default, and expose the API definition under [/swagger endpoint](http://localhost:8082/swagger). 

You can also run this via docker with the following commands:

```bash
docker-compose build payments
docker-compose up payments
```

The payment service expects a basket id and total payment value, and will process the payment for the basket.

It immediately returns "payment id", and the operation will proceed asynchronously. Your service can simply remove the basket when payment id is returned. However, it should NOT remove the basket if payment service returns an error. An error should be returned from your endpoint, and the service should keep the basket and allow the customer to try again later.
