package com.trendyol.checkout.mapper;

import com.trendyol.checkout.dto.AddItemDTO;
import com.trendyol.checkout.dto.CartDetailsDTO;
import com.trendyol.checkout.entity.CheckoutCart;

import java.util.List;
import java.util.stream.Collectors;


public class CartMapper {
    public static CartDetailsDTO cartToDTO(CheckoutCart checkoutCart) {
        CartDetailsDTO cartDetailsDTO = new CartDetailsDTO();
        cartDetailsDTO.setAppliedPromotionId(checkoutCart.getAppliedPromotionId());
        cartDetailsDTO.setTotalDiscount(checkoutCart.getTotalDiscount());
        cartDetailsDTO.setTotalPrice(checkoutCart.getTotalPrice());

        List<AddItemDTO> itemDTOs = checkoutCart.getItems().stream().map(ItemMapper::itemToDto).collect(Collectors.toList());
        cartDetailsDTO.setItems(itemDTOs);

        return cartDetailsDTO;
    }
}
