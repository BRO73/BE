package com.example.restaurant_management.common.exception.promotion;

public class PromotionNotFoundException extends PromotionValidationException {
    public PromotionNotFoundException() {
        super("Mã giảm giá không tồn tại");
    }
}
