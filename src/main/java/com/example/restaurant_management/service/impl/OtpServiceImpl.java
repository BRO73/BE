//package com.example.restaurant_management.service.impl;
//
//import com.example.restaurant_management.entity.Customer;
//import com.example.restaurant_management.repository.CustomerRepository;
//import com.example.restaurant_management.service.OtpService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
//@Service
//@RequiredArgsConstructor
//public class OtpServiceImpl implements OtpService {
//
//    private final CustomerRepository customerRepository;
//    private final InfobipConfig infobipConfig;
//
//    @Override
//    public void generateOtp(String phoneNumber) {
//        String otp = String.valueOf(new Random().nextInt(900000) + 100000);
//        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);
//
//        customerRepository.findByPhoneNumber(phoneNumber).ifPresentOrElse(
//                customer -> {
//                    customer.setOtpCode(otp);
//                    customer.setOtpExpiryTime(expiryTime);
//                    customerRepository.save(customer);
//                },
//                () -> {
//                    Customer newCustomer = Customer.builder()
//                            .phoneNumber(phoneNumber)
//                            .otpCode(otp)
//                            .otpExpiryTime(expiryTime)
//                            .build();
//                    customerRepository.save(newCustomer);
//                }
//        );
//
//        // ✅ Gửi OTP qua Infobip
//        try {
//            sendSmsWithInfobip(phoneNumber, otp);
//            System.out.println("📩 OTP sent to " + phoneNumber);
//        } catch (Exception e) {
//            System.err.println("❌ Failed to send OTP via Infobip: " + e.getMessage());
//            throw new RuntimeException("Failed to send OTP via Infobip", e);
//        }
//    }
//
//    private void sendSmsWithInfobip(String to, String messageText) {
//        String baseUrl = infobipConfig.getApiBaseUrl();
//        if (baseUrl == null || baseUrl.isBlank()) {
//            throw new RuntimeException("Infobip base URL is not configured!");
//        }
//        baseUrl = baseUrl.trim();
//        if (!baseUrl.startsWith("http")) {
//            baseUrl = "https://" + baseUrl;
//        }
//
//        // ✅ Format số điện thoại sang dạng E.164 (+84...)
//        String formattedPhone = to.trim();
//        if (formattedPhone.startsWith("0")) {
//            formattedPhone = "+84" + formattedPhone.substring(1);
//        } else if (!formattedPhone.startsWith("+84")) {
//            formattedPhone = "+84" + formattedPhone;
//        }
//
//        String url = baseUrl + "/sms/2/text/advanced";
//        RestTemplate restTemplate = new RestTemplate();
//
//        Map<String, Object> message = Map.of(
//                "from", infobipConfig.getSender(),
//                "destinations", List.of(Map.of("to", formattedPhone)), // ✅ dùng số đã format
//                "text", "Your verification code is: " + messageText
//        );
//
//        Map<String, Object> payload = Map.of("messages", List.of(message));
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", infobipConfig.getApiKey());
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
//
//        System.out.println("✅ Full URL: " + url);
//        System.out.println("📨 Sending OTP to: " + formattedPhone);
//
//        ResponseEntity<String> response = restTemplate.exchange(
//                url,
//                HttpMethod.POST,
//                request,
//                String.class
//        );
//
//        System.out.println("📨 Infobip response: " + response.getBody());
//    }
//
//
//    @Override
//    public boolean validateOtp(String phoneNumber, String otp) {
//        return customerRepository.findByPhoneNumber(phoneNumber)
//                .filter(c -> c.getOtpCode() != null && c.getOtpCode().equals(otp))
//                .filter(c -> c.getOtpExpiryTime().isAfter(LocalDateTime.now()))
//                .isPresent();
//    }
//}
