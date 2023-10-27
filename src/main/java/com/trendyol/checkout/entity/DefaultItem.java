package com.trendyol.checkout.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class DefaultItem extends Item {
    @OneToMany(mappedBy = "defaultItem", cascade = CascadeType.ALL)
    private List<VasItem> vasItems;
}
