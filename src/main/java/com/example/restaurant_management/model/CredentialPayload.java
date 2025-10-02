package com.example.restaurant_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CredentialPayload {
    private Long userId;
    private String email;
    private String fullName;
    private String storeName;
}
