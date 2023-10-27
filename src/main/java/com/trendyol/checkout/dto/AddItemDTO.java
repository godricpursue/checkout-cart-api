package com.trendyol.checkout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddItemDTO {
    private int itemId;
    private int categoryId;
    private int sellerId;
    private double price;
    private int quantity;


}
