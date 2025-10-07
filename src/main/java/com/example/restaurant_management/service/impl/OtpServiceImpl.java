package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.entity.Customer;
import com.example.restaurant_management.repository.CustomerRepository;
import com.example.restaurant_management.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final CustomerRepository customerRepository;

    @Override
    public void generateOtp(String phoneNumber) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        customerRepository.findByPhoneNumber(phoneNumber).ifPresentOrElse(
                customer -> {
                    customer.setOtpCode(otp);
                    customer.setOtpExpiryTime(expiryTime);
                    customerRepository.save(customer);
                },
                () -> {
                    // Nếu khách hàng chưa có -> tạo bản ghi OTP tạm (không cần user_id lúc này)
                    Customer newCustomer = Customer.builder()
                            .phoneNumber(phoneNumber)
                            .otpCode(otp)
                            .otpExpiryTime(expiryTime)
                            .build();
                    customerRepository.save(newCustomer);
                }
        );

        // TODO: gửi OTP qua SMS thực tế (Twilio, Zalo, v.v.)
        System.out.println("📩 OTP gửi tới " + phoneNumber + " là: " + otp);
    }

    @Override
    public boolean validateOtp(String phoneNumber, String otp) {
        return customerRepository.findByPhoneNumber(phoneNumber)
                .filter(c -> c.getOtpCode() != null && c.getOtpCode().equals(otp))
                .filter(c -> c.getOtpExpiryTime().isAfter(LocalDateTime.now()))
                .isPresent();
    }
}
