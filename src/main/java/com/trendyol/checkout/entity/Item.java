package com.trendyol.checkout.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public abstract class Item {
    @Id
    private Integer itemId;
    private int categoryId;
    private int sellerId;
    private double price;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "checkout_cart_id")
    private CheckoutCart checkoutCart;
    public static final int ITEM_QUANTITY_PER_INPUT = 10;
    public static final String QUANTITY_ERROR_PER_INPUT_MESSAGE = "Item quantity exceeds limit.";
}
