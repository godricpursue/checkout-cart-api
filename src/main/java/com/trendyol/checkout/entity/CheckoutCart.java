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

    public static final String REMOVAL_SUCCESS_MESSAGE = "Item removed from cart.";

    public static final String RESET_SUCCESS_MESSAGE = "Cart reset successfully.";

    public static double getTotalCartValue(CheckoutCart checkoutCart) {
        double totalValue = 0.0;
        if (checkoutCart == null) {
            return totalValue;
        }



        for (Item item : checkoutCart.getItems()) {
            totalValue += item.getPrice() * item.getQuantity();

            if (item instanceof DefaultItem defaultItem) {
                if (VasItem.applicableCategories.contains(defaultItem.getCategoryId())) {
                    for (VasItem vasItem : defaultItem.getVasItems()) {
                        totalValue += vasItem.getPrice() * vasItem.getQuantity();
                    }
                }
            }
        }

        return totalValue;
    }

}
