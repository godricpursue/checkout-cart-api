package com.trendyol.checkout.service.impl;

import com.trendyol.checkout.repository.CheckoutCartRepository;
import com.trendyol.checkout.repository.ItemRepository;
import com.trendyol.checkout.service.CheckoutCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckoutCartServiceImpl implements CheckoutCartService {
    private final CheckoutCartRepository checkoutCartRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public CheckoutCartServiceImpl(CheckoutCartRepository checkoutCartRepository, ItemRepository itemRepository) {
        this.checkoutCartRepository = checkoutCartRepository;
        this.itemRepository = itemRepository;
    }
}
