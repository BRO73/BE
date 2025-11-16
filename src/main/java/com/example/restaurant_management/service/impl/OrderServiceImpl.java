package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.constant.ErrorEnum;
import com.example.restaurant_management.common.exception.RestaurantException;
import com.example.restaurant_management.dto.request.AddItemsRequest;
import com.example.restaurant_management.dto.request.MergeOrderRequest;
import com.example.restaurant_management.dto.request.OrderRequest;
import com.example.restaurant_management.dto.response.OrderResponse;
import com.example.restaurant_management.dto.response.SplitOrderRequest;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        return orderRepository.findAllWithDetails().stream()  // ✅ Chỉ 1 query
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findByIdWithDetails(id)  // ✅ Chỉ 1 query
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
        return orderRepository.findByTableIdWithDetails(tableId).stream()  // ✅
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrdersByStatus(String status) {
        return orderRepository.findByStatusWithDetails(status).stream()  // ✅
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrdersByStaff(Long staffId) {
        return orderRepository.findByStaffUserIdWithDetails(staffId).stream()  // ✅
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrdersBetween(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByCreatedAtBetweenWithDetails(start, end).stream()  // ✅
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getActiveOrdersByTable(Long tableId) {
        TableEntity table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        return orderRepository.findByTableAndStatusWithDetails(table, "PENDING").stream()  // ✅
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

    @Override
    @Transactional
    public OrderResponse mergeOrders(MergeOrderRequest request) {

        Order sourceOrder = orderRepository.findById(request.getSourceOrderId())
                .orElseThrow(() -> new RestaurantException("Source order not found"));

        Order targetOrder = orderRepository.findById(request.getTargetOrderId())
                .orElseThrow(() -> new RestaurantException("Target order not found"));

        if (!"PENDING".equals(sourceOrder.getStatus())) {
            throw new RestaurantException("Source order must be PENDING");
        }
        if (!"PENDING".equals(targetOrder.getStatus())) {
            throw new RestaurantException("Target order must be PENDING");
        }

        // 3. Validate: TẤT CẢ OrderDetails phải COMPLETED
        List<OrderDetail> allDetails = new ArrayList<>();
        allDetails.addAll(sourceOrder.getOrderDetails());
        allDetails.addAll(targetOrder.getOrderDetails());

        boolean hasIncompleteItem = allDetails.stream()
                .anyMatch(detail -> !"COMPLETED".equals(detail.getStatus()));

        if (hasIncompleteItem) {
            throw new RestaurantException("Có món chưa hoàn thành, không thể gộp order");
        }

        // 4. Gộp tất cả OrderDetails từ sourceOrder sang targetOrder
        for (OrderDetail detail : sourceOrder.getOrderDetails()) {
            detail.setOrder(targetOrder);
            orderDetailRepository.save(detail);
        }

        // 5. Cập nhật sourceOrder status = MERGED
        sourceOrder.setStatus("MERGED");
        orderRepository.save(sourceOrder);

        // 6. Tính lại totalAmount cho targetOrder
        BigDecimal totalAmount = targetOrder.getOrderDetails().stream()
                .map(detail -> detail.getPriceAtOrder().multiply(BigDecimal.valueOf(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        targetOrder.setTotalAmount(totalAmount);
        Order mergedOrder = orderRepository.save(targetOrder);


        // 7. Trả về OrderResponse sử dụng OrderMapper
        return orderMapper.toResponse(mergedOrder);
    }

    @Override
    @Transactional
    public OrderResponse splitOrder(SplitOrderRequest request) {

        // 1. Lấy order gốc
        Order sourceOrder = orderRepository.findById(request.getSourceOrderId())
                .orElseThrow(() -> new RestaurantException("Source order not found"));

        // 2. Validate: Order phải PENDING
        if (!"PENDING".equals(sourceOrder.getStatus())) {
            throw new RestaurantException("Order must be PENDING to split");
        }

        // 3. Lấy table mới
        TableEntity newTable = tableRepository.findById(request.getNewTableId())
                .orElseThrow(() -> new RestaurantException("New table not found"));

        // 4. Validate split items
        Map<Long, Integer> splitItems = request.getSplitItems();
        if (splitItems.isEmpty()) {
            throw new RestaurantException("Split items cannot be empty");
        }

        // 5. Tạo order mới
        Order newOrder = Order.builder()
                .table(newTable)
                .staffUser(sourceOrder.getStaffUser())
                .customerUser(sourceOrder.getCustomerUser())
                .status(sourceOrder.getStatus())
                .notes(sourceOrder.getNotes())
                .totalAmount(BigDecimal.ZERO)
                .orderDetails(new ArrayList<>())
                .build();

        newOrder = orderRepository.save(newOrder);

        // 6. Xử lý split cho từng MenuItem
        List<OrderDetail> movedOrEmptyDetails = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : splitItems.entrySet()) {
            Long menuItemId = entry.getKey();
            Integer quantityToSplit = entry.getValue();

            // Validate quantity
            if (quantityToSplit <= 0) {
                throw new RestaurantException("Quantity to split must be greater than 0");
            }

            // Tìm tất cả OrderDetails có cùng menuItemId (sắp xếp theo thứ tự tạo - FIFO)
            List<OrderDetail> matchingDetails = sourceOrder.getOrderDetails().stream()
                    .filter(d -> d.getMenuItem().getId().equals(menuItemId))
                    .sorted((d1, d2) -> d1.getCreatedAt().compareTo(d2.getCreatedAt()))
                    .toList();

            if (matchingDetails.isEmpty()) {
                throw new RestaurantException("MenuItem not found in order: " + menuItemId);
            }

            // Tính tổng quantity có sẵn
            int totalAvailable = matchingDetails.stream()
                    .mapToInt(OrderDetail::getQuantity)
                    .sum();

            if (quantityToSplit > totalAvailable) {
                throw new RestaurantException("Quantity to split exceeds available quantity for menuItem: " + menuItemId);
            }

            // Trừ dần quantity từ các OrderDetails (FIFO)
            int remainingToSplit = quantityToSplit;
            MenuItem menuItem = matchingDetails.get(0).getMenuItem();
            BigDecimal priceAtOrder = matchingDetails.get(0).getPriceAtOrder();
            String commonStatus = matchingDetails.get(0).getStatus();

            for (OrderDetail detail : matchingDetails) {
                if (remainingToSplit == 0) break;

                if (detail.getQuantity() <= remainingToSplit) {
                    // Lấy hết OrderDetail này
                    remainingToSplit -= detail.getQuantity();
                    detail.setOrder(newOrder);
                    orderDetailRepository.save(detail);
                    movedOrEmptyDetails.add(detail);
                } else {
                    // Lấy một phần
                    detail.setQuantity(detail.getQuantity() - remainingToSplit);
                    orderDetailRepository.save(detail);

                    // Tạo OrderDetail mới cho order mới
                    OrderDetail newDetail = OrderDetail.builder()
                            .order(newOrder)
                            .menuItem(detail.getMenuItem())
                            .quantity(remainingToSplit)
                            .priceAtOrder(detail.getPriceAtOrder())
                            .status(detail.getStatus())
                            .notes(detail.getNotes())
                            .build();
                    orderDetailRepository.save(newDetail);

                    remainingToSplit = 0;
                }
            }
        }

        // 7. Validate: Order gốc phải còn ít nhất 1 OrderDetail
        long remainingDetailsCount = sourceOrder.getOrderDetails().stream()
                .filter(d -> d.getOrder().getId().equals(sourceOrder.getId()))
                .count();

        if (remainingDetailsCount == 0) {
            throw new RestaurantException("Cannot split all items. Source order must have at least 1 item remaining");
        }

        // 8. Tính lại totalAmount cho cả 2 orders
        // Order gốc
        BigDecimal sourceTotal = sourceOrder.getOrderDetails().stream()
                .map(detail -> detail.getPriceAtOrder().multiply(BigDecimal.valueOf(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        sourceOrder.setTotalAmount(sourceTotal);
        orderRepository.save(sourceOrder);

        // Order mới
        BigDecimal newTotal = newOrder.getOrderDetails().stream()
                .map(detail -> detail.getPriceAtOrder().multiply(BigDecimal.valueOf(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        newOrder.setTotalAmount(newTotal);
        Order savedNewOrder = orderRepository.save(newOrder);

        // 9. Trả về OrderResponse của order mới sử dụng OrderMapper
        return orderMapper.toResponse(savedNewOrder);
    }
}