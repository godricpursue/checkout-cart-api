package com.trendyol.checkout.entity;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class DigitalItem extends Item{
    public static final int DIGITAL_ITEM_CATEGORY_ID = 7889;
    public static final int DIGITAL_ITEM_QUANTITY_PER_INPUT = 5;
    public static final String HAS_DEFAULT_ITEM_ERROR_MESSAGE = "Cannot add DigitalItem when cart has DefaultItems.";
    public static final String QUANTITY_ERROR_PER_INPUT_MESSAGE = "Digital item quantity exceeds limit.";
}
