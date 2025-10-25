package com.example.restaurant_management.dto.request;

import java.util.List;
public record SplitBillRequest(
        Long sessionId,
        List<SplitItemRequest> items
) {}
