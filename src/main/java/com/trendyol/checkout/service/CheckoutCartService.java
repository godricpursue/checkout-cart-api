package com.trendyol.checkout.service;

import com.trendyol.checkout.dto.AddItemDTO;
import com.trendyol.checkout.dto.ResponseDTO;

public interface CheckoutCartService {
     ResponseDTO addItem(AddItemDTO itemDTO);
}
