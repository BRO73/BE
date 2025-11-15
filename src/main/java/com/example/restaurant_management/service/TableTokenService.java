package com.example.restaurant_management.service;

public interface TableTokenService {
    String hashTableId(Long tableId);
    boolean matches(Long tableId, String token);
    Long resolveTableId(String token);
}
