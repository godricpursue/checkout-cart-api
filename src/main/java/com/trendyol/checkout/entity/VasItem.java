package com.trendyol.checkout.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
public class VasItem{
    private int itemId;
    private int categoryId;
    private int sellerId;
    private double price;
    private int quantity;
    @Id
    private int vasItemId;
    public static final int VAS_ITEM_CATEGORY_ID = 3242;
    public static final int VAS_ITEM_SELLER_ID = 5003;
    public static final String VAS_ITEM_WRONG_ENDPOINT_MESSAGE = "Cannot add VasItem using this endpoint.";
    @ManyToOne
    @JoinColumn(name = "default_item_id")
    private DefaultItem defaultItem;
    @ManyToOne
    @JoinColumn(name = "checkout_cart_id")
    private CheckoutCart checkoutCart;
}
