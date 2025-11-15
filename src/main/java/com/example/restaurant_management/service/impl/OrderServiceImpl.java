package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.constant.ErrorEnum;
import com.example.restaurant_management.common.exception.RestaurantException;
import com.example.restaurant_management.dto.request.AddItemsRequest;
import com.example.restaurant_management.dto.request.OrderRequest;
import com.example.restaurant_management.dto.response.OrderResponse;
import com.example.restaurant_management.entity.*;
import com.example.restaurant_management.mapper.OrderMapper;
import com.example.restaurant_management.repository.*;
import com.example.restaurant_management.service.KitchenService;
import com.example.restaurant_management.service.OrderService;
import com.example.restaurant_management.util.SecurityUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final TableRepository tableRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final KitchenService kitchenService;
    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return orderMapper.toResponse(order);

    }
    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request,
                                     Authentication authentication) {
        Long userId = SecurityUtils.getCurrentUserId(authentication);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestaurantException(ErrorEnum.USER_NOT_FOUND));

        Order order = orderMapper.toEntity(request);
        Set<Role> role = roleRepository.findByUserId(userId)
                .orElseThrow(() -> new RestaurantException(ErrorEnum.ROLE_NOT_FOUND));
        if (role.stream().anyMatch(r -> r.getName().equals("CUSTOMER"))) {
            order.setCustomerUser(user);
        } else if (role.stream().anyMatch(r ->
                r.getName().equals("WAITSTAFF")
                        || r.getName().equals("CASHIER")
                        || r.getName().equals("ADMIN")
        )) {
            order.setStaffUser(user);
        }

        order = orderRepository.save(order);
        return orderMapper.toResponse(order);
    }

    @Transactional
    public OrderResponse updateOrder(Long id, OrderRequest request) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found");
        }
        Order order = orderMapper.toEntity(request);
        order.setId(id);
        order = orderRepository.save(order);
        return orderMapper.toResponse(order);
    }

    @Override
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found");
        }
        orderRepository.deleteById(id);
    }

    @Override
    public List<OrderResponse> getOrdersByTable(Long tableId) {
        return orderRepository.findByTableId(tableId).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrdersByStaff(Long staffId) {
        return orderRepository.findByStaffUserId(staffId).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrdersBetween(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByCreatedAtBetween(start, end).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getActiveOrdersByTable(Long tableId) {
        TableEntity table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        return orderRepository.findByTableAndStatus(table, "PENDING").stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public OrderResponse addItemsToOrder(Long orderId, AddItemsRequest request, Authentication authentication) {
        // 1. Kiểm tra order tồn tại
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order không tồn tại với id: " + orderId));

        // 2. Kiểm tra order phải ở trạng thái PENDING (chưa gửi bếp hoặc đang chờ)
        if (!order.getStatus().equals("PENDING")) {
            throw new RuntimeException("Chỉ có thể thêm món vào order đang ở trạng thái PENDING");
        }

        // 3. Xử lý từng món trong request
        for (AddItemsRequest.OrderDetailItem itemRequest : request.getItems()) {
            // Lấy thông tin món ăn
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("MenuItem không tồn tại với id: " + itemRequest.getMenuItemId()));

            // Kiểm tra món có available không
            if (!menuItem.getStatus().equalsIgnoreCase("Available")) {
                throw new RuntimeException("Món " + menuItem.getName() + " hiện không có sẵn");
            }

            // Tạo OrderDetail mới
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setMenuItem(menuItem);
            orderDetail.setQuantity(itemRequest.getQuantity());
            orderDetail.setPriceAtOrder(menuItem.getPrice()); // Lấy giá hiện tại
            orderDetail.setNotes(itemRequest.getSpecialRequirements());
            orderDetail.setStatus("PENDING"); // Món mới vào luôn ở trạng thái PENDING

            // Lưu OrderDetail
            orderDetailRepository.save(orderDetail);

            // Thêm vào order
            order.getOrderDetails().add(orderDetail);
        }

        // 4. Cập nhật tổng tiền của order
        BigDecimal newTotal = order.getOrderDetails().stream()
                .map(item -> item.getPriceAtOrder().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(newTotal);

        // 5. Cập nhật thời gian
        order.setUpdatedAt(LocalDateTime.now());

        // 6. Lưu order
        Order savedOrder = orderRepository.save(order);

        kitchenService.notifyBoardUpdate();

        // 7. Trả về response
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse linkCustomerToOrder(Long orderId, Long userId) { // Đổi tên param cho rõ
        // 1. Tìm Order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RestaurantException("Order not found"));

//        // 2. Kiểm tra Order đã có khách chưa (Logic của bạn là đúng)
//        if (order.getCustomerUser() != null) {
//            throw new RestaurantException("Order has customer"); // "Đơn này đã có khách"
//        }

        // 3. Tìm User (không phải Customer)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestaurantException("User not found"));

        // 4. Gán khách (User) vào
        order.setCustomerUser(user);

        // 5. Lưu và trả về DTO
        Order savedOrder = orderRepository.save(order);

        // === SỬA LỖI BIÊN DỊCH ===
        // Map entity đã lưu sang DTO trước khi trả về
        return orderMapper.toResponse(savedOrder);
    }

    @Transactional
    public OrderResponse unlinkCustomer(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RestaurantException("Order not found with id: " + orderId));

        order.setCustomerUser(null);
        Order savedOrder = orderRepository.save(order);

        return orderMapper.toResponse(savedOrder);
    }
}