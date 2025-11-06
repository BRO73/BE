package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.UpdateUserAccountRequest;
import com.example.restaurant_management.dto.response.RestaurantResponse;
import com.example.restaurant_management.dto.response.UserAccountResponse;
import com.example.restaurant_management.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-accounts")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;

    @GetMapping
    public ResponseEntity<RestaurantResponse<List<UserAccountResponse>>> list() {
        return RestaurantResponse.ok(userAccountService.list(), "Fetched");
    }

    @PutMapping("/{userId}")
    public ResponseEntity<RestaurantResponse<UserAccountResponse>> update(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserAccountRequest req
    ) {
        return RestaurantResponse.ok(userAccountService.update(userId, req), "Updated");
    }
}
