package com.example.restaurant_management.common.exception.promotion;

public class PromotionExpiredException extends PromotionValidationException {
    public PromotionExpiredException() {
        super("Mã giảm giá đã hết hạn");
    }
}
