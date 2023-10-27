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
    @JoinColumn(name = "checkoutCart_id")
    private CheckoutCart checkoutCart;
}
