package com.trendyol.checkout.repository;

import com.trendyol.checkout.entity.CheckoutCart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckoutCartRepository extends JpaRepository<CheckoutCart, String> {
}
