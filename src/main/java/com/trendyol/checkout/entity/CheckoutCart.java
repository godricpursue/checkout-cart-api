package com.trendyol.checkout.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "checkout_cart")
public class CheckoutCart {
    public static final String CART_REFERANCE = "SINGLE_CART";
    @Id
    private String referance = CART_REFERANCE;

    @OneToMany(mappedBy = "checkoutCart", cascade = CascadeType.ALL)
    private List<Item> items = new ArrayList<>();

    private double totalPrice;

    private int appliedPromotionId;

    private double totalDiscount;

    public static final String SUCCESS_MESSAGE = "Item added to checkoutCart.";
    public static final double MAX_CART_VALUE = 500000;
    public static final String MAX_CART_VALUE_ERROR_MESSAGE = "Cart value cannot exceed 500000.";

    public static final int MAX_UNIQUE_ITEM_COUNT = 10;
    public static final String MAX_UNIQUE_ITEM_COUNT_ERROR_MESSAGE = "Cart cannot have more than 10 unique items.";

    public static final int MAX_QUANTITY_PER_CART = 30;
    public static final String MAX_QUANTITY_PER_CART_ERROR_MESSAGE = "Cannot exceed 30 products in total.";

    public static final String CART_NOT_FOUND_ERROR_MESSAGE = "Cart not found.";

}
