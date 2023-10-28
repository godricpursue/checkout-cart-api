package com.trendyol.checkout.controller;

import com.trendyol.checkout.dto.AddItemDTO;
import com.trendyol.checkout.dto.AddVasItemDTO;
import com.trendyol.checkout.dto.RemoveItemDTO;
import com.trendyol.checkout.dto.ResponseDTO;
import com.trendyol.checkout.service.CheckoutCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
public class CheckoutCartController {

    private final CheckoutCartService checkoutCartService;
    @Autowired
    public CheckoutCartController(CheckoutCartService checkoutCartService) {
        this.checkoutCartService = checkoutCartService;
    }

    @PostMapping("/item")
    public ResponseDTO addItem(@RequestBody AddItemDTO itemDTO) {
        return checkoutCartService.addItem(itemDTO);
    }

    @PostMapping("/vasItem")
    public ResponseDTO addVasItem(@RequestBody AddVasItemDTO vasItemDTO) {
        return checkoutCartService.addVasItem(vasItemDTO);
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseDTO removeItem(@PathVariable int itemId) {
        RemoveItemDTO removeItemDTO = new RemoveItemDTO();
        removeItemDTO.setItemId(itemId);
        return checkoutCartService.removeItem(removeItemDTO);
    }
}
