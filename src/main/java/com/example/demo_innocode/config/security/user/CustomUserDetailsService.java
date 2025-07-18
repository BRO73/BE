package com.example.demo_innocode.config.security.user;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo_innocode.common.constant.ErrorEnum;
import com.example.demo_innocode.common.exception.InnoException;
import com.example.demo_innocode.constant.RoleConstant;
import com.example.demo_innocode.entity.User;
import com.example.demo_innocode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).
                orElseThrow(() -> new InnoException(ErrorEnum.USER_NOT_FOUND));

        return UserDetailsImpl.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .enabled(Boolean.FALSE.equals(user.isDeleted()))
                .authorities(getUserAuthorities(user))
                .build();
    }

    private List<SimpleGrantedAuthority> getUserAuthorities(User user) {
        User newUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new InnoException(ErrorEnum.USER_NOT_FOUND));

        return Collections.singletonList(new SimpleGrantedAuthority(RoleConstant.ROLE_PREFIX + newUser.getRole()));

    }
}