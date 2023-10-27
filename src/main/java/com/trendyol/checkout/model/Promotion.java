package com.trendyol.checkout.model;

import com.trendyol.checkout.entity.CheckoutCart;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Data
public class Promotion {

    public static final int SAME_SELLER_PROMOTION_ID = 9909;
    public static final int CATEGORY_PROMOTION_ID = 5676;
    public static final int TOTAL_PRICE_PROMOTION_ID = 1232;

    private int promotionId;
    private Double discount;


    public Promotion(int promotionId, Double discount) {
        this.promotionId = promotionId;
        this.discount = discount;
    }

    public static Promotion applyPromotion(CheckoutCart checkoutCart) {
        List<Promotion> applicablePromotions = new ArrayList<>();

        if (checkoutCart.getItems().stream().allMatch(item -> item.getSellerId() == checkoutCart.getItems().get(0).getSellerId())) {
            applicablePromotions.add(new Promotion(SAME_SELLER_PROMOTION_ID, 0.10 * checkoutCart.getTotalPrice()));
        }


        if (checkoutCart.getItems().stream().anyMatch(item -> item.getCategoryId() == 3003L)) {
            applicablePromotions.add(new Promotion(CATEGORY_PROMOTION_ID, 0.05 * checkoutCart.getTotalPrice()));
        }

        // Check for TotalPricePromotion
        double totalValue = checkoutCart.getTotalPrice();
        if (totalValue < 5000) {
            applicablePromotions.add(new Promotion(TOTAL_PRICE_PROMOTION_ID, 250.0));
        } else if (totalValue < 10000) {
            applicablePromotions.add(new Promotion(TOTAL_PRICE_PROMOTION_ID, 500.0));
        } else if (totalValue < 50000) {
            applicablePromotions.add(new Promotion(TOTAL_PRICE_PROMOTION_ID, 1000.0));
        } else {
            applicablePromotions.add(new Promotion(TOTAL_PRICE_PROMOTION_ID, 2000.0));
        }

        Promotion bestPromotion = applicablePromotions.stream()
                .max(Comparator.comparingDouble(Promotion::getDiscount))
                .orElse(null);


        if ((totalValue - bestPromotion.getDiscount()) > CheckoutCart.MAX_CART_VALUE) {
            throw new IllegalArgumentException(CheckoutCart.MAX_CART_VALUE_ERROR_MESSAGE);
        }

        return bestPromotion;
    }
}

