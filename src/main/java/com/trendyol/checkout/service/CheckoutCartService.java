package com.trendyol.checkout.service;

import com.trendyol.checkout.dto.*;

public interface CheckoutCartService {
     ResponseDTO addItem(AddItemDTO itemDTO);
     ResponseDTO addVasItem(AddVasItemDTO vasItemDTO);
     ResponseDTO removeItem(RemoveItemDTO removeItemDTO);
     ResponseDTO resetCart();
     CartResponseDTO displayCart();
}
