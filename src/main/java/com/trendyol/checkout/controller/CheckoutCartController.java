package com.trendyol.checkout.controller;

import com.trendyol.checkout.service.CheckoutCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class CheckoutCartController {

    @Autowired
    private CheckoutCartService checkoutCartService;


}
