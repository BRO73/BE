package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.constant.ClaimConstant;
import com.example.restaurant_management.dto.request.RegisterCustomerRequest;
import com.example.restaurant_management.dto.response.OtpLoginResponse;
import com.example.restaurant_management.entity.Customer;
import com.example.restaurant_management.model.CredentialPayload;
import com.example.restaurant_management.repository.CustomerRepository;
import com.example.restaurant_management.service.JWTService;
import com.example.restaurant_management.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerAuthService {

    private final OtpService otpService;
    private final CustomerRepository customerRepository;
    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;

    public void sendOtp(String phoneNumber) {
        otpService.generateOtp(phoneNumber);
    }

    public OtpLoginResponse verifyOtp(String phoneNumber, String otp) {
        if (!otpService.validateOtp(phoneNumber, otp)) {
            throw new RuntimeException("Invalid OTP or expired");
        }

        return customerRepository.findByPhoneNumber(phoneNumber)
                .map(customer -> {
                    // Nếu đã có user gắn vào thì mới sinh JWT
                    String jwt = null;
                    if (customer.getUser() != null) {
                        jwt = generateJwt(phoneNumber);
                    }
                    return new OtpLoginResponse(
                            customer.getUser() != null ? "EXISTING_CUSTOMER" : "NEW_CUSTOMER",
                            jwt,
                            customer
                    );
                })
                .orElseThrow(() -> new RuntimeException("Customer not found after OTP validation"));
    }

    public OtpLoginResponse registerCustomer(RegisterCustomerRequest request) {
        Customer customer = customerRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("Customer not found. OTP verification required."));

        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customerRepository.save(customer);

        String jwt = generateJwt(request.getPhoneNumber());
        return new OtpLoginResponse("REGISTERED", jwt, customer);
    }

    private String generateJwt(String phoneNumber) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNumber);

        Map<String, Object> claims = new HashMap<>();
        claims.put(ClaimConstant.TOKEN_TYPE, ClaimConstant.ACCESS_TOKEN);
        claims.put("roles", userDetails.getAuthorities());

        // 🔁 Tạo đối tượng Authentication từ UserDetails
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        return jwtService.generateToken(claims, authentication);
    }

    private Map<String, Object> buildClaimsAccessToken(Authentication authentication) {
        Map<String, Object> claims = buildClaimsFromAuthentication(authentication);
        claims.put(ClaimConstant.TOKEN_TYPE, ClaimConstant.ACCESS_TOKEN);
        return claims;
    }

    private Map<String, Object> buildClaimsRefreshToken(Authentication authentication) {
        Map<String, Object> claims = buildClaimsFromAuthentication(authentication);
        claims.put(ClaimConstant.TOKEN_TYPE, ClaimConstant.REFRESH_TOKEN);
        return claims;
    }

    private Map<String, Object> buildClaimsFromAuthentication(Authentication authentication) {
        final Map<String, Object> claims = new LinkedHashMap<>();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        CredentialPayload credentialPayload = (CredentialPayload) authentication.getCredentials();

        claims.put(ClaimConstant.AUTH_USER_ROLES, roles);
        claims.put(ClaimConstant.AUTH_USER_ID, credentialPayload.getUserId());
        claims.put(ClaimConstant.AUTH_STORE_NAME, credentialPayload.getStoreName());

        return claims;
    }
}
