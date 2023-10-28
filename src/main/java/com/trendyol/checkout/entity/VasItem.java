package com.trendyol.checkout.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Entity
@Data
@Getter
public class VasItem{
    @Id
    private int vasItemId;
    private int itemId;
    private int categoryId;
    private int sellerId;
    private double price;
    private int quantity;

    private static final int FURNITURE_CATEGORY_ID = 1001;
    private static final int ELECTRONIC_CATEGORY_ID = 3003;

    public static List<Integer> applicableCategories = Arrays.asList(FURNITURE_CATEGORY_ID, ELECTRONIC_CATEGORY_ID);

    @ManyToOne
    @JoinColumn(name = "default_item_id")
    private DefaultItem defaultItem;
    @ManyToOne
    @JoinColumn(name = "checkout_cart_id")
    private CheckoutCart checkoutCart;
    public static final int VAS_ITEM_CATEGORY_ID = 3242;
    public static final int VAS_ITEM_SELLER_ID = 5003;



    public static final String VAS_ITEM_WRONG_ENDPOINT_MESSAGE = "Cannot add VasItem using this endpoint.";
    public static final String SUCCESS_MESSAGE = "VasItem added to DefaultItem.";
    public static final String INVALID_VAS_ITEM_DETAILS_MESSAGE = "Invalid VasItem details.";
    public static final String VAS_ITEM_PRICE_HIGHER_THAN_DEFAULT_ITEM_PRICE_MESSAGE = "VasItem price cannot be higher than the DefaultItem price.";
    public static final String NO_ITEMS_IN_THE_CART_TO_ATTACH_A_VAS_ITEM_MESSAGE = "No items in the cart to attach a VasItem.";
    public static final String VAS_ITEM_CATEGORY_ERROR_MESSAGE = "VasItem can only be added to Furniture or Electronics.";
}
