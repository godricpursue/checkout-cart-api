package com.trendyol.checkout.repository;

import com.trendyol.checkout.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Integer> {
}
