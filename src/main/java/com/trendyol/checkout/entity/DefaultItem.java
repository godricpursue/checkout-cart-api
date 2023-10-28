package com.trendyol.checkout.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultItem extends Item {
    @OneToMany(mappedBy = "defaultItem", cascade = CascadeType.ALL)
    private List<VasItem> vasItems = new ArrayList<>();

    public static final int VAS_ITEM_SPACE_LIMIT = 3;
    public static final String HAS_DIGITAL_ITEM_ERROR_MESSAGE = "Cannot add DefaultItem when cart has DigitalItem.";
    public static final String DEFAULT_ITEM_NOT_FOUND_MESSAGE = "Default item not found.";
    public static final String EXCEEDED_VASITEM_MESSAGE = "A DefaultItem can have a maximum of 3 VasItems.";

}
