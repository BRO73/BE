package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.UpdateAvailabilityRequest;
import com.example.restaurant_management.dto.response.MenuAvailabilityResponse;
import com.example.restaurant_management.entity.MenuItem;
import com.example.restaurant_management.service.MenuAvailabilityService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu")
public class MenuAvailabilityController {

    private final MenuAvailabilityService service;

    public MenuAvailabilityController(MenuAvailabilityService service) {
        this.service = service;
    }

    // Đọc: user đã đăng nhập (bếp/phục vụ/manager đều xem được)
    @GetMapping("/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Map<String, Object>>> list() {
        List<MenuItem> items = service.listAll();

        // Dùng HashMap để đảm bảo kiểu value là Object (tránh lỗi Incompatible types)
        List<Map<String, Object>> dto = items.stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", m.getId());
            map.put("name", m.getName());
            map.put("price", m.getPrice());
            map.put("available",
                    m.getStatus() != null && m.getStatus().equalsIgnoreCase("available"));
            return map;
        }).toList();

        return ResponseEntity.ok(dto);
    }

    // Ghi: chỉ ADMIN/MANAGER/OWNER được thay đổi
    @PatchMapping("/items/{id}/availability")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','OWNER')")
    public ResponseEntity<MenuAvailabilityResponse> setAvailability(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAvailabilityRequest req
    ) {
        boolean now = service.setAvailability(id, Boolean.TRUE.equals(req.getAvailable()));
        return ResponseEntity.ok(new MenuAvailabilityResponse(id, now));
    }
}
