package com.example.restaurant_management.common.exception.promotion;

public class PromotionNotActiveException extends PromotionValidationException {
    public PromotionNotActiveException() {
        super("Mã giảm giá không khả dụng");
    }
}
