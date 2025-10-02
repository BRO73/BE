package com.example.restaurant_management.config.security.custom;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

public class CustomAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private Object credentials;
    private String storeName;

    public CustomAuthenticationToken(Object principal, Object credentials, String storeName) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.storeName = storeName;
        setAuthenticated(false);
    }

    public CustomAuthenticationToken(Object principal, Object credentials, String storeName,
                                     Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.storeName = storeName;
        super.setAuthenticated(true); // must use super, as we override
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String StoreName) {
        this.storeName = StoreName;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        credentials = null;
    }
}