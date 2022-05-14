package com.wayfair.products;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get a product by id.",
            description =
                    "In this stub implementation all product ids have to start with WF, otherwise error will be returned.<br/>" +
                            "The following product ids have hardcoded product values: WF0123, WF01234, WF012345.<br/>" +
                            "Ids starting with WF1 will generate a random product with no offers and price up to 2000.<br/> " +
                            "Ids starting with WF2 will generate a random product with 2-for-1 offer and price up to 1000.<br/>" +
                            "Ids starting with WF3 will generate a random product with 3-for-2 offer and price up to 100.<br/>" +
                            "Any other ids will return product not found.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200",
            description = "Found product",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)})
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@NotNull @PathVariable String id) {
        if (!id.startsWith("WF")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Id has to start with WF"));
        }
        return productService.getProductById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
