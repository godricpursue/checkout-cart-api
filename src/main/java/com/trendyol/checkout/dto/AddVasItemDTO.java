package com.trendyol.checkout.dto;

import lombok.Data;

@Data
public class AddVasItemDTO {
    private int itemId;
    private int vasItemId;
    private int categoryId;
    private int sellerId;
    private double price;
    private int quantity;
}
