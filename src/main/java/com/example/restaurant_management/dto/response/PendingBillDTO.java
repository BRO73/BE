package com.example.restaurant_management.dto.response;
import java.math.BigDecimal;
public record PendingBillDTO(
        Long menuItemId,
        String menuItemName,
        BigDecimal quantityUnpaid, // Sẽ là số lẻ (vd: 1.5)
        BigDecimal amountUnpaid
) {}