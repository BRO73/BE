package com.example.restaurant_management.dto.response;

public class MenuAvailabilityResponse {
    private Long menuItemId;
    private boolean available;

    public MenuAvailabilityResponse(Long menuItemId, boolean available) {
        this.menuItemId = menuItemId;
        this.available = available;
    }

    public Long getMenuItemId() { return menuItemId; }
    public boolean isAvailable() { return available; }
}
