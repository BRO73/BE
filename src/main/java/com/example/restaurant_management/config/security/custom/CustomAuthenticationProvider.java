package com.example.restaurant_management.config.security.custom;

import com.example.restaurant_management.common.constant.ErrorEnum;
import com.example.restaurant_management.common.exception.RestaurantException;
import com.example.restaurant_management.config.security.user.UserDetailsImpl;
import com.example.restaurant_management.model.CredentialPayload;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        final String username = (String) authentication.getPrincipal();
        final String password = (String) authentication.getCredentials();
        if (!(authentication instanceof CustomAuthenticationToken customToken)) {
            throw new RestaurantException(ErrorEnum.INVALID_CREDENTIALS);
        }
        final String storeName = customToken.getStoreName();
        final UserDetails user = userDetailsService.loadUserByUsernameAndStoreName(username,storeName);

        if (StringUtils.isBlank(password) || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RestaurantException(ErrorEnum.PASSWORD_INCORRECT);
        }

        if (Boolean.FALSE.equals(user.isEnabled())) {
            throw new RestaurantException(ErrorEnum.USER_DISABLED);
        }

        CredentialPayload credentialPayload = CredentialPayload.builder()
                .email(((UserDetailsImpl) user).getEmail())
                .userId(((UserDetailsImpl) user).getUserId())
                .fullName(((UserDetailsImpl) user).getFullName())
                .storeName(((UserDetailsImpl) user).getStoreName())
                .build();

        return new UsernamePasswordAuthenticationToken(user.getUsername(), credentialPayload, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}