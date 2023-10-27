package com.trendyol.checkout.entity;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Data
public class VasItem extends Item{
    private int vasItemId;
    @ManyToOne
    @JoinColumn(name = "default_item_id")
    private DefaultItem defaultItem;
}
