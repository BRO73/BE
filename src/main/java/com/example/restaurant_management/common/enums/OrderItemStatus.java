package com.example.restaurant_management.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum OrderItemStatus {
    PENDING,
    IN_PROGRESS,
    DONE,
    SERVED,

    CANCELED;

    @JsonCreator
    public static OrderItemStatus from(Object raw) {
        if (raw == null) return null;
        String s = raw.toString().trim();

        String norm = s
                .replace('-', ' ')
                .replace('.', ' ')
                .replace('/', ' ')
                .replace('\\', ' ')
                .trim()
                .replaceAll("\\s+", "_")
                .toUpperCase(Locale.ROOT);

        // chấp nhận cả CANCELLED (British) -> CANCELED
        if ("CANCELLED".equals(norm)) {
            norm = "CANCELED";
        }

        return OrderItemStatus.valueOf(norm);
    }

    @JsonValue
    public String toJson() {
        return name();
    }
}
