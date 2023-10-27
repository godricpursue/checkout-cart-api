package com.trendyol.checkout.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class DefaultItem extends Item {
    public static final String HAS_DIGITAL_ITEM_ERROR_MESSAGE = "Cannot add DefaultItem when cart has DigitalItem.";
    @OneToMany(mappedBy = "defaultItem", cascade = CascadeType.ALL)
    private List<VasItem> vasItems;
}
