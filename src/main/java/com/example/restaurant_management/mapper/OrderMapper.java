package com.example.restaurant_management.mapper;

import com.example.restaurant_management.common.enums.OrderItemStatus;
import com.example.restaurant_management.dto.request.OrderRequest;
import com.example.restaurant_management.dto.request.OrderDetailRequest;
import com.example.restaurant_management.dto.response.*;
import com.example.restaurant_management.entity.*;
import com.example.restaurant_management.repository.MenuItemRepository;
import com.example.restaurant_management.repository.TableRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class OrderMapper {
    private final TableRepository tableRepository;
    private final MenuItemRepository menuItemRepository;

    public Order toEntity(OrderRequest request) {
        Order order = Order.builder()
                .status("PENDING")
                .notes(request.getNote())
                .build();
        order.setCreatedAt(LocalDateTime.now());

        TableEntity table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new RuntimeException("Table not found"));
        order.setTable(table);

        List<OrderDetail> orderDetails = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderDetailRequest item : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(item.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found"));

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setMenuItem(menuItem);
            detail.setQuantity(item.getQuantity());
            detail.setStatus(String.valueOf(OrderItemStatus.PENDING));
            detail.setNotes(item.getSpecialRequirements());
            detail.setPriceAtOrder(menuItem.getPrice());

            totalAmount = totalAmount.add(menuItem.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            orderDetails.add(detail);
        }

        order.setOrderDetails(orderDetails);
        order.setTotalAmount(totalAmount);

        return order;
    }

    public OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setNote(order.getNotes());

        // Table
        TableResponse tableResponse = new TableResponse();
        tableResponse.setId(order.getTable().getId());
        tableResponse.setTableNumber(order.getTable().getTableNumber());
        tableResponse.setCapacity(order.getTable().getCapacity());
        response.setTable(tableResponse);

        // ✅ Staff - Lấy từ staffUser.staff (đã eager load)
        if (order.getStaffUser() != null) {
            Staff staff = order.getStaffUser().getStaff();
            if (staff != null) {
                StaffResponse staffResponse = new StaffResponse();
                staffResponse.setId(staff.getId());
                staffResponse.setName(staff.getFullName());
                response.setStaff(staffResponse);
            } else if (order.getStaffUser().getUsername() != null) {
                // Fallback nếu không có Staff profile
                StaffResponse staffResponse = new StaffResponse();
                staffResponse.setId(order.getStaffUser().getId());
                staffResponse.setName(order.getStaffUser().getUsername());
                response.setStaff(staffResponse);
            }
        }

        // ✅ Customer - Lấy từ customerUser.customer (đã eager load)
        if (order.getCustomerUser() != null) {
            Customer customer = order.getCustomerUser().getCustomer();
            if (customer != null) {
                response.setCustomerUserId(order.getCustomerUser().getId());
                response.setCustomerPhone(customer.getPhoneNumber());
                response.setCustomerName(customer.getFullName());
            } else {
                System.err.println("Warning: Order " + order.getId() +
                        " has a User but no corresponding Customer profile found.");
            }
        }

        // Order details
        List<OrderDetailResponse> items = order.getOrderDetails().stream()
                .map(detail -> {
                    OrderDetailResponse itemResponse = new OrderDetailResponse();
                    itemResponse.setId(detail.getId());

                    MenuItemResponse menuItemResponse = new MenuItemResponse();
                    menuItemResponse.setId(detail.getMenuItem().getId());
                    menuItemResponse.setName(detail.getMenuItem().getName());
                    menuItemResponse.setPrice(detail.getPriceAtOrder());

                    itemResponse.setMenuItem(menuItemResponse);
                    itemResponse.setQuantity(detail.getQuantity());
                    itemResponse.setPrice(detail.getPriceAtOrder());
                    itemResponse.setSpecialRequirements(detail.getNotes());
                    return itemResponse;
                })
                .collect(Collectors.toList());

        response.setItems(items);

        return response;
    }
}