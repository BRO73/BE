package com.example.restaurant_management.common.exception.promotion;

public class PromotionUsageLimitExceededException extends PromotionValidationException {
    public PromotionUsageLimitExceededException() {
        super("Mã giảm giá đã hết số lần sử dụng");
    }
}
