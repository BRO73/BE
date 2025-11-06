package com.example.restaurant_management.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum OrderItemStatus {
    PENDING,
    IN_PROGRESS,
    DONE,       // FE map sang "ready"
    SERVED,
    CANCELED;

    /** Chuẩn hoá chuỗi về UPPER_SNAKE_CASE “an toàn merge” */
    private static String normalize(String s) {
        return s == null ? "" :
                s.trim()
                        .replace('-', ' ')
                        .replace('.', ' ')
                        .replace('/', ' ')
                        .replace('\\', ' ')
                        .replaceAll("\\s+", "_")
                        .toUpperCase(Locale.ROOT);
    }


    @JsonCreator
    public static OrderItemStatus from(Object raw) {
        if (raw == null) return null;

        String norm = normalize(raw.toString());

        switch (norm) {
            case "PROCESSING":
            case "INPROGRESS":
            case "IN_PROGRESS":
                return IN_PROGRESS;

            case "READY":
            case "READY_TO_SERVE":
            case "READYTOSERVE":
                return DONE;

            case "CANCELLED":
                return CANCELED;
        }

        try {
            return OrderItemStatus.valueOf(norm);
        } catch (IllegalArgumentException ex) {
            throw ex;
        }
    }

    /** JsonValue: luôn xuất ra UPPER_SNAKE_CASE chuẩn */
    @JsonCreator
    public static OrderItemStatus fromJson(String raw) {
        if (raw == null) throw new IllegalArgumentException("status null");
        String norm = raw.trim().toUpperCase(Locale.ROOT).replaceAll("[-\\s]+", "_");
        if ("READY".equals(norm) || "READY_TO_SERVE".equals(norm)) return DONE;
        return OrderItemStatus.valueOf(norm);
    }
    @JsonValue public String toJson() { return name(); }

}
