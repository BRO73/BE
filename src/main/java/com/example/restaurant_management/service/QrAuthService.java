package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.response.TokenResponse;

public interface QrAuthService {
    TokenResponse qrLogin(Long tableId);
}
