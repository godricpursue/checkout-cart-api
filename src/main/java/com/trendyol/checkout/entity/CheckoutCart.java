package com.trendyol.checkout.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class CheckoutCart {

    public static final String CART_REFERANCE = "SINGLE_CART" ;

    @Id
    private String referance = CART_REFERANCE;

    @OneToMany(mappedBy = "checkoutCart", cascade = CascadeType.ALL)
    private List<Item> items;

    private double totalPrice;

    private int appliedPromotionId;

    private double totalDiscount;
}
