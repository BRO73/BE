package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.constant.ErrorEnum;
import com.example.restaurant_management.common.exception.RestaurantException;
import com.example.restaurant_management.config.security.user.UserDetailsImpl;
import com.example.restaurant_management.constant.ClaimConstant;
import com.example.restaurant_management.dto.request.RegisterCustomerRequest;
import com.example.restaurant_management.dto.response.OtpLoginResponse;
import com.example.restaurant_management.dto.response.TokenResponse;
import com.example.restaurant_management.entity.Customer;
import com.example.restaurant_management.entity.Role;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.entity.UserRole;
import com.example.restaurant_management.model.CredentialPayload;
import com.example.restaurant_management.repository.CustomerRepository;
import com.example.restaurant_management.repository.RoleRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.repository.UserRoleRepository;
import com.example.restaurant_management.service.JWTService;
import com.example.restaurant_management.service.OtpService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.apache.naming.factory.SendMailFactory;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private final OtpService otpService;
    private final CustomerRepository customerRepository;
    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public void sendOtp(String phoneNumber) {
        otpService.generateOtp(phoneNumber);
    }

    public TokenResponse verifyOtp(String phoneNumber, String otp) {
        if (!otpService.validateOtp(phoneNumber, otp)) {
            throw new RuntimeException("Invalid OTP or expired");
        }

        return customerRepository.findByPhoneNumber(phoneNumber)
                .map(customer -> {
                    if (customer.getUser() == null) {
                        throw new RestaurantException(ErrorEnum.USER_NOT_FOUND);
                    }
                    return TokenResponse.builder()
                            .accessToken(generateJwt(ClaimConstant.ACCESS_TOKEN, customer.getPhoneNumber()))
                            .refreshToken(generateJwt(ClaimConstant.REFRESH_TOKEN, customer.getPhoneNumber()))
                            .expiresIn(System.currentTimeMillis() + jwtExpiration)
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Customer not found after OTP validation"));
    }

    @Transactional
    public TokenResponse registerCustomer(RegisterCustomerRequest request) {

        Customer customer = customerRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("Customer not found. OTP verification required."));
        User user = User.builder()
                .customer(customer)
                .username(customer.getPhoneNumber())
                .build();
        userRepository.save(user);

        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setUser(user);
        customerRepository.save(customer);

        Role role = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Role not found."));
        UserRole userRole = UserRole.builder()
                .roleId(role.getId())
                .userId(user.getId())
                .build();
        userRoleRepository.save(userRole);

        return TokenResponse.builder()
                .accessToken(generateJwt(ClaimConstant.ACCESS_TOKEN, customer.getPhoneNumber()))
                .refreshToken(generateJwt(ClaimConstant.REFRESH_TOKEN, customer.getPhoneNumber()))
                .expiresIn(System.currentTimeMillis() + jwtExpiration)
                .build();
    }

    private String generateJwt(String tokenType, String phoneNumber) {
        UserDetails user = userDetailsService.loadUserByUsername(phoneNumber);

        Map<String, Object> claims = new HashMap<>();

        claims.put(ClaimConstant.TOKEN_TYPE, tokenType);

        CredentialPayload credentialPayload = CredentialPayload.builder()
                .userId(((UserDetailsImpl) user).getUserId())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                credentialPayload,
                user.getAuthorities()
        );
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        claims.put(ClaimConstant.AUTH_USER_ROLES, roles);

        return jwtService.generateToken(claims, authentication);
    }
}

