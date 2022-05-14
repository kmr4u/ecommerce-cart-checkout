package com.wayfair.payments;

import com.wayfair.payments.ExceptionControllerAdvice.ServiceError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Request a payment for given basket and value",
            description = "This stub implementation will return a random payment id for most baskets.<br/>" +
                    "To simulate service errors, it will return an error if basket id starts with `eee`<br/>" +
                    "It will also return an error if multiple payments for the same basket are requested")
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "Payment request accepted, returns payment id"),
            @ApiResponse(responseCode = "400",
                    description = "Invalid request, for example multiple payments for same basket requested",
                    content = @Content(schema = @Schema(implementation = ServiceError.class))),
            @ApiResponse(responseCode = "500",
                    description = "Payment service encountered an unexpected internal error",
                    content = @Content(schema = @Schema(implementation = ServiceError.class))),
            @ApiResponse(responseCode = "503",
                    description = "Payment service encountered a temporary internal error",
                    content = @Content(schema = @Schema(implementation = ServiceError.class)))})
    @PostMapping
    public PaymentResponse requestPayment(@NotNull @Valid @RequestBody PaymentRequest request) {
        log.info("Processing payment {}]", request);
        String paymentId = paymentService.requestPayment(request.getBasketId(), request.getPaymentValue());
        return new PaymentResponse(request.getBasketId(), paymentId);
    }

    @Data
    public static class PaymentRequest {
        @NotNull
        private String basketId;
        @NotNull
        @Min(0)
        private BigDecimal paymentValue;
    }

    @Data
    public static class PaymentResponse {
        @NotNull
        private final String basketId;
        @NotNull
        private final String paymentId;
    }
}
