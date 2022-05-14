package com.wayfair.checkout.controller;

import com.wayfair.checkout.exception.CheckoutServiceExceptionHandler;
import com.wayfair.checkout.model.AddProductRequest;
import com.wayfair.checkout.model.BasketItem;
import com.wayfair.checkout.service.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/basket/items")
public class CheckoutController {

    @Autowired
    private CheckoutService service;

    @Operation(summary = "Add a product identified by id with specified quantity to a customer's basket identified by uuid.",
               description =
                "The product id has to be available in the inventory, otherwise error will be returned.<br/>" +
                "The following product ids are available currently: WF0123, WF01234, WF012345.<br/>" +
                "In this implementation the customers are loaded statically using a resource file.<br/>" +
                "The following Customer Ids are available currently: 2e699cad-5a2d-44e4-bd4f-2bd67811390d, 3eb7f680-5324-4db0-a2dd-f118c1fe3e16, 659286e4-94d5-452a-bd22-9719d2aa9a97, 21c48bbe-b592-4cd8-922d-7a10101fe45f <br/>"+
                "Providing a non existing customer id or product id will result in an error.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "All the products added to a customer basket", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BasketItem.class)))}),
    @ApiResponse(responseCode = "404", description = "Customer Id not found or Product Id not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CheckoutServiceExceptionHandler.ServiceError.class))}),
    @ApiResponse(responseCode = "503", description = "Service not available", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CheckoutServiceExceptionHandler.ServiceError.class))})})
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BasketItem>> addProductToBasket(@NotNull @RequestHeader("customerId") UUID customerId, @RequestBody AddProductRequest request) {
        assert customerId != null && !customerId.equals("");
        return new ResponseEntity<>(service.addProductToBasket(request, customerId), HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Get all the products and their quantity added to a customer's basket identified by uuid.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "All the products added to a customer basket", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BasketItem.class)))}),
    @ApiResponse(responseCode = "404", description = "Customer Id not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CheckoutServiceExceptionHandler.ServiceError.class))}),})
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<BasketItem>> getBasketItems(@NotNull @RequestHeader("customerId") UUID customerId) {
        assert customerId != null && !customerId.equals("");
        return new ResponseEntity<>(service.getBasketItems(customerId), HttpStatus.OK);
    }

    @Operation(summary = "Get the total price of all the products added to a customer's basket identified by uuid.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Total price of all the products added to a customer basket", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BigDecimal.class))}),
    @ApiResponse(responseCode = "404", description = "Customer Id not found", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CheckoutServiceExceptionHandler.ServiceError.class))})})
    @RequestMapping(value = "/total", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BigDecimal> getBasketItemsPrice(@NotNull @RequestHeader("customerId") UUID customerId) {
        assert customerId != null && !customerId.equals("");
        return new ResponseEntity<>(service.getBasketItemsPrice(customerId), HttpStatus.OK);
    }

}
