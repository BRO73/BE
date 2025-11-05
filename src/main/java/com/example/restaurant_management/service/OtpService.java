package com.example.restaurant_management.service;

public interface OtpService {
    void generateOtp(String phoneNumber);
    boolean validateOtp(String phoneNumber, String otp);
}
