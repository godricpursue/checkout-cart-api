package com.trendyol.checkout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CartResponseDTO {
    private boolean result;
    private CartDetailsDTO cartDetails;
}