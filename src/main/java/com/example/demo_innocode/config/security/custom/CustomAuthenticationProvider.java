package com.example.demo_innocode.config.security.custom;

import com.example.demo_innocode.common.constant.ErrorEnum;
import com.example.demo_innocode.common.exception.InnoException;
import com.example.demo_innocode.config.security.user.UserDetailsImpl;
import com.example.demo_innocode.model.CredentialPayload;
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
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        final String username = (String) authentication.getPrincipal();
        final String password = (String) authentication.getCredentials();
        final UserDetails user = userDetailsService.loadUserByUsername(username);

        if (StringUtils.isBlank(password) || !passwordEncoder.matches(password, user.getPassword())) {
            throw new InnoException(ErrorEnum.PASSWORD_INCORRECT);
        }

        if (Boolean.FALSE.equals(user.isEnabled())) {
            throw new InnoException(ErrorEnum.USER_DISABLED);
        }

        CredentialPayload credentialPayload = CredentialPayload.builder()
                .email(((UserDetailsImpl) user).getEmail())
                .userId(((UserDetailsImpl) user).getUserId())
                .fullName(((UserDetailsImpl) user).getFullName())
                .build();

        return new UsernamePasswordAuthenticationToken(user.getUsername(), credentialPayload, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
