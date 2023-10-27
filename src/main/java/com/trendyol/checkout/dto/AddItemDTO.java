package com.trendyol.checkout.dto;

import lombok.Data;

@Data
public class AddItemDTO {
    private int itemId;
    private int categoryId;
    private int sellerId;
    private double price;
    private int quantity;
}
