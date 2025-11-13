package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.response.RestaurantResponse;
import com.example.restaurant_management.dto.request.CreateStaffAccountRequest;
import com.example.restaurant_management.dto.request.UpdateUserAccountRequest;
import com.example.restaurant_management.dto.response.StaffAccountResponse;
import com.example.restaurant_management.service.StaffAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff-accounts")
@RequiredArgsConstructor
public class StaffAccountController {

    private final StaffAccountService staffAccountService;

    /**
     * Lấy danh sách tất cả account của staff
     */
    @GetMapping
    public ResponseEntity<RestaurantResponse<List<StaffAccountResponse>>> list() {
        List<StaffAccountResponse> data = staffAccountService.list();
        return RestaurantResponse.ok(data, "Get staff accounts successfully");
    }

    /**
     * Tạo account cho 1 staff đã tồn tại
     */
    @PostMapping
    public ResponseEntity<RestaurantResponse<StaffAccountResponse>> create(
            @Valid @RequestBody CreateStaffAccountRequest req
    ) {
        StaffAccountResponse data = staffAccountService.create(req);
        // Vì bạn KHÔNG có RestaurantResponse.created(), dùng ok() luôn
        return RestaurantResponse.ok(data, "Create staff account successfully");
    }

    /**
     * Cập nhật username / password / role cho userId
     */
    @PutMapping("/{userId}")
    public ResponseEntity<RestaurantResponse<StaffAccountResponse>> update(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserAccountRequest req
    ) {
        StaffAccountResponse data = staffAccountService.update(userId, req);
        return RestaurantResponse.ok(data, "Update staff account successfully");
    }

    /**
     * Xoá account của 1 user (detach staff + xoá user + xoá user_roles)
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<RestaurantResponse<Void>> delete(
            @PathVariable Long userId
    ) {
        staffAccountService.delete(userId);
        return RestaurantResponse.ok(null, "Delete staff account successfully");
    }
}
