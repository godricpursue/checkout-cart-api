package com.trendyol.checkout.controller;

import com.trendyol.checkout.dto.*;
import com.trendyol.checkout.service.CheckoutCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/carts")
@Tag(name = "Cart Operations", description = "Operations related to the checkout cart.")
public class CheckoutCartController {

    private final CheckoutCartService checkoutCartService;

    @Autowired
    public CheckoutCartController(CheckoutCartService checkoutCartService) {
        this.checkoutCartService = checkoutCartService;
    }

    @PostMapping("/item")
    @Operation(summary = "Add an item to the cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item added successfully"),
            @ApiResponse(responseCode = "400", description = "Failed to add item because of -this reason-.")
    })
    public ResponseEntity<?> addItem(@RequestBody AddItemDTO itemDTO) {
        ResponseDTO response = checkoutCartService.addItem(itemDTO);
        return responseToEntity(response);
    }

    @PostMapping("/vasItem")
    @Operation(summary = "Add a VasItem to an existing default item in the cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "VasItem added successfully"),
            @ApiResponse(responseCode = "400", description = "Failed to add VasItem because of -this reason-. ")
    })
    public ResponseEntity<?> addVasItem(@RequestBody AddVasItemDTO vasItemDTO) {
        ResponseDTO response = checkoutCartService.addVasItem(vasItemDTO);
        return responseToEntity(response);
    }

    @DeleteMapping("/item/{itemId}")
    @Operation(summary = "Remove an item from the cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item removed successfully"),
            @ApiResponse(responseCode = "400", description = "Failed to remove item because of -this reason-.")
    })
    public ResponseEntity<?> removeItem(@PathVariable int itemId) {
        RemoveItemDTO removeItemDTO = new RemoveItemDTO();
        removeItemDTO.setItemId(itemId);
        ResponseDTO response = checkoutCartService.removeItem(removeItemDTO);
        return responseToEntity(response);
    }

    @PostMapping("/reset")
    @Operation(summary = "Reset the cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart reset successfully"),
            @ApiResponse(responseCode = "400", description = "Failed to reset the cart because of -this reason-.")
    })
    public ResponseEntity<?> resetCart() {
        ResponseDTO response = checkoutCartService.resetCart();
        return responseToEntity(response);
    }

    @GetMapping("/display")
    @Operation(summary = "Display the cart contents")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart displayed successfully"),
            @ApiResponse(responseCode = "400", description = "Failed to display the cart because of -this reason-.")
    })
    public ResponseEntity<?> displayCart() {
        CartResponseDTO response = checkoutCartService.displayCart();
        return responseToEntity(response);
    }

    private ResponseEntity<?> responseToEntity(ResponseDTO response) {
        return response.isResult()
                ? new ResponseEntity<>(response, HttpStatus.OK)
                : new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // Overloaded utility function to handle CartResponseDTO
    private ResponseEntity<?> responseToEntity(CartResponseDTO response) {
        return response.isResult()
                ? new ResponseEntity<>(response, HttpStatus.OK)
                : new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
