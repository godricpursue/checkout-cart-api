package com.trendyol.checkout.repository;

import com.trendyol.checkout.entity.VasItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VasItemRepository extends JpaRepository<VasItem, Integer> {
}
