package com.trendyol.checkout.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO {
    public static final boolean FAILED = false;
    public static final boolean SUCCESS = true;
    private boolean result;
    private String message;
}