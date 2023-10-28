package com.trendyol.checkout.dto;

import lombok.Data;

import java.util.List;

@Data
public class CartDetailsDTO {
    private List<AddItemDTO> items;
    private int appliedPromotionId;
    private double totalDiscount;
    private double totalPrice;
}
