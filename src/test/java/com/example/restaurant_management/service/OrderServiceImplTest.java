package com.example.restaurant_management.service;

// Import cho createOrder
import com.example.restaurant_management.common.constant.ErrorEnum;
import com.example.restaurant_management.common.exception.RestaurantException;
import com.example.restaurant_management.dto.request.OrderRequest;
import com.example.restaurant_management.dto.response.OrderResponse;
import com.example.restaurant_management.entity.Order;
import com.example.restaurant_management.entity.Role;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.mapper.OrderMapper;
import com.example.restaurant_management.repository.OrderRepository;
import com.example.restaurant_management.repository.RoleRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.service.impl.OrderServiceImpl;
import com.example.restaurant_management.util.SecurityUtils;

// Import cho addItemsToOrder
import com.example.restaurant_management.dto.request.AddItemsRequest;
import com.example.restaurant_management.entity.MenuItem;
import com.example.restaurant_management.entity.OrderDetail;
import com.example.restaurant_management.repository.MenuItemRepository;
import com.example.restaurant_management.repository.OrderDetailRepository;

// Import chung của JUnit & Mockito
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.DisplayName.class)
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    // === Mocks cho cả 2 nhóm test ===
    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private OrderMapper orderMapper;
    @Mock private Authentication authentication;

    // --- Mocks MỚI cho addItemsToOrder ---
    @Mock private MenuItemRepository menuItemRepository;
    @Mock private OrderDetailRepository orderDetailRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    // === Captors ===
    @Captor private ArgumentCaptor<Order> orderArgumentCaptor;
    // --- Captor MỚI ---
    @Captor private ArgumentCaptor<OrderDetail> orderDetailCaptor;


    // === Biến cho createOrder ===
    private Long fakeUserId = 1L;
    private User mockUser;
    private OrderRequest request;
    private Order mappedOrder; // 'Order' thô sau khi map
    private Order savedOrder;  // 'Order' giả sau khi save
    private OrderResponse mockResponse;

    // === Biến MỚI cho addItemsToOrder ===
    private MockedStatic<LocalDateTime> mockedTime;
    private final LocalDateTime fakeNow = LocalDateTime.of(2025, 10, 20, 10, 30, 0);
    private Long fakeOrderId = 1L;
    private Order mockOrder; // Order dùng cho 'addItems'
    private MenuItem mockMenuItem1;
    private MenuItem mockMenuItem2;
    private AddItemsRequest addItemsRequest;


    @BeforeEach
    void setUp() {
        // --- Setup cho createOrder ---
        request = new OrderRequest();
        mockUser = new User();
        mockUser.setId(fakeUserId);
        mappedOrder = new Order();
        savedOrder = new Order();
        savedOrder.setId(100L);
        mockResponse = new OrderResponse();
        mockResponse.setId(100L);

        // --- Setup MỚI cho addItemsToOrder ---
        mockedTime = mockStatic(LocalDateTime.class);
        mockedTime.when(LocalDateTime::now).thenReturn(fakeNow);

        mockOrder = new Order();
        mockOrder.setId(fakeOrderId);
        mockOrder.setStatus("PENDING");

        // --- SỬA LỖI Ở ĐÂY ---
        mockOrder.setOrderDetails(new ArrayList<>()); // Dùng ArrayList thật để .add()
        // --- KẾT THÚC SỬA ---

        mockOrder.setTotalAmount(BigDecimal.ZERO);

        mockMenuItem1 = new MenuItem();
        mockMenuItem1.setId(10L);
        mockMenuItem1.setName("Phở Bò");
        mockMenuItem1.setStatus("Available");
        mockMenuItem1.setPrice(new BigDecimal("50.00"));

        mockMenuItem2 = new MenuItem();
        mockMenuItem2.setId(11L);
        mockMenuItem2.setName("Coca");
        mockMenuItem2.setStatus("Available");
        mockMenuItem2.setPrice(new BigDecimal("10.00"));

        addItemsRequest = new AddItemsRequest();
    }

    @AfterEach
    void tearDown() {
        // Phải close static mock sau mỗi test
        // (MockedStatic của SecurityUtils được close trong try-with-resources)
        if (mockedTime != null) {
            mockedTime.close();
        }
    }

    // ===============================================
    // ===       TESTS CHO createOrder             ===
    // ===============================================

//    @Test
//    @DisplayName("Test 1 [CreateOrder]: Fails_When_UserNotFound")
//    void Fails_When_UserNotFound() {
//        // === 1. GIVEN ===
//        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
//            mockedSecurity.when(() -> SecurityUtils.getCurrentUserId(authentication))
//                    .thenReturn(fakeUserId);
//            when(userRepository.findById(fakeUserId)).thenReturn(Optional.empty());
//
//            // === 2. WHEN & 3. THEN ===
//            RestaurantException exception = assertThrows(
//                    RestaurantException.class,
//                    () -> orderService.createOrder(request, authentication)
//            );
//            assertThat(exception.getErrorCode()).isEqualTo(ErrorEnum.USER_NOT_FOUND.getCode());
//            verify(roleRepository, never()).findByUserId(any());
//            verify(orderRepository, never()).save(any());
//        }
//    }
//
//    @Test
//    @DisplayName("Test 2 [CreateOrder]: Fails_When_RoleNotFound")
//    void Fails_When_RoleNotFound() {
//        // === 1. GIVEN ===
//        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
//            mockedSecurity.when(() -> SecurityUtils.getCurrentUserId(authentication))
//                    .thenReturn(fakeUserId);
//            when(userRepository.findById(fakeUserId)).thenReturn(Optional.of(mockUser));
//            when(roleRepository.findByUserId(fakeUserId)).thenReturn(Optional.empty());
//            when(orderMapper.toEntity(request)).thenReturn(mappedOrder);
//
//            // === 2. WHEN & 3. THEN ===
//            RestaurantException exception = assertThrows(
//                    RestaurantException.class,
//                    () -> orderService.createOrder(request, authentication)
//            );
//            assertThat(exception.getErrorCode()).isEqualTo(ErrorEnum.ROLE_NOT_FOUND.getCode());
//            verify(orderRepository, never()).save(any());
//        }
//    }
//
//    @Test
//    @DisplayName("Test 3 [CreateOrder]: Success_AsCustomer")
//    void Success_AsCustomer() {
//        // === 1. GIVEN ===
//        Role customerRole = new Role();
//        customerRole.setName("CUSTOMER");
//        Set<Role> roles = Set.of(customerRole);
//
//        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
//            mockedSecurity.when(() -> SecurityUtils.getCurrentUserId(authentication))
//                    .thenReturn(fakeUserId);
//            when(userRepository.findById(fakeUserId)).thenReturn(Optional.of(mockUser));
//            when(roleRepository.findByUserId(fakeUserId)).thenReturn(Optional.of(roles));
//            when(orderMapper.toEntity(request)).thenReturn(mappedOrder);
//            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
//            when(orderMapper.toResponse(savedOrder)).thenReturn(mockResponse);
//
//            // === 2. WHEN ===
//            OrderResponse response = orderService.createOrder(request, authentication);
//
//            // === 3. THEN ===
//            assertThat(response).isNotNull();
//            assertThat(response.getId()).isEqualTo(100L);
//            verify(orderRepository, times(1)).save(orderArgumentCaptor.capture());
//            Order capturedOrder = orderArgumentCaptor.getValue();
//            assertThat(capturedOrder.getCustomerUser()).isEqualTo(mockUser);
//            assertThat(capturedOrder.getStaffUser()).isNull();
//        }
//    }
//
//    @Test
//    @DisplayName("Test 4 [CreateOrder]: Success_AsWaitstaff")
//    void Success_AsWaitstaff() {
//        // === 1. GIVEN ===
//        Role staffRole = new Role();
//        staffRole.setName("WAITSTAFF");
//        Set<Role> roles = Set.of(staffRole);
//
//        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
//            mockedSecurity.when(() -> SecurityUtils.getCurrentUserId(authentication))
//                    .thenReturn(fakeUserId);
//            when(userRepository.findById(fakeUserId)).thenReturn(Optional.of(mockUser));
//            when(roleRepository.findByUserId(fakeUserId)).thenReturn(Optional.of(roles));
//            when(orderMapper.toEntity(request)).thenReturn(mappedOrder);
//            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
//            when(orderMapper.toResponse(savedOrder)).thenReturn(mockResponse);
//
//            // === 2. WHEN ===
//            orderService.createOrder(request, authentication);
//
//            // === 3. THEN ===
//            verify(orderRepository, times(1)).save(orderArgumentCaptor.capture());
//            Order capturedOrder = orderArgumentCaptor.getValue();
//            assertThat(capturedOrder.getCustomerUser()).isNull();
//            assertThat(capturedOrder.getStaffUser()).isEqualTo(mockUser);
//        }
//    }
//
//    @Test
//    @DisplayName("Test 5 [CreateOrder]: Success_AsAdmin")
//    void Success_AsAdmin() {
//        // === 1. GIVEN ===
//        Role adminRole = new Role();
//        adminRole.setName("ADMIN");
//        Set<Role> roles = Set.of(adminRole);
//
//        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
//            mockedSecurity.when(() -> SecurityUtils.getCurrentUserId(authentication))
//                    .thenReturn(fakeUserId);
//            when(userRepository.findById(fakeUserId)).thenReturn(Optional.of(mockUser));
//            when(roleRepository.findByUserId(fakeUserId)).thenReturn(Optional.of(roles));
//            when(orderMapper.toEntity(request)).thenReturn(mappedOrder);
//            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
//            when(orderMapper.toResponse(savedOrder)).thenReturn(mockResponse);
//
//            // === 2. WHEN ===
//            orderService.createOrder(request, authentication);
//
//            // === 3. THEN ===
//            verify(orderRepository, times(1)).save(orderArgumentCaptor.capture());
//            Order capturedOrder = orderArgumentCaptor.getValue();
//            assertThat(capturedOrder.getCustomerUser()).isNull();
//            assertThat(capturedOrder.getStaffUser()).isNull();
//        }
//    }
//
//    @Test
//    @DisplayName("Test 6 [CreateOrder]: Fails_When_RoleIsInvalid")
//    void Fails_When_RoleIsInvalid() {
//        // === 1. GIVEN ===
//        Role managerRole = new Role();
//        managerRole.setName("MANAGER");
//        Set<Role> roles = Set.of(managerRole);
//
//        try (MockedStatic<SecurityUtils> mockedSecurity = mockStatic(SecurityUtils.class)) {
//            mockedSecurity.when(() -> SecurityUtils.getCurrentUserId(authentication))
//                    .thenReturn(fakeUserId);
//            when(userRepository.findById(fakeUserId)).thenReturn(Optional.of(mockUser));
//            when(roleRepository.findByUserId(fakeUserId)).thenReturn(Optional.of(roles));
//            when(orderMapper.toEntity(request)).thenReturn(mappedOrder);
//
//            // === 2. WHEN & 3. THEN ===
//            RestaurantException exception = assertThrows(
//                    RestaurantException.class,
//                    () -> orderService.createOrder(request, authentication)
//            );
//            // Giả sử mã lỗi bạn dùng là ACCESS_DENIED
//            assertThat(exception.getErrorCode()).isEqualTo(ErrorEnum.ACCESS_DENIED.getCode());
//            verify(orderRepository, never()).save(any());
//        }
//    }


    // ===============================================
    // ===     TESTS MỚI CHO addItemsToOrder       ===
    // ===============================================

    @Test
    @DisplayName("Test 1 [AddItems]: Fails_When_OrderNotFound")
    void addItems_Fails_When_OrderNotFound() {
        // 1. GIVEN
        when(orderRepository.findById(fakeOrderId)).thenReturn(Optional.empty());

        // 2. WHEN & 3. THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.addItemsToOrder(fakeOrderId, addItemsRequest, authentication);
        });

        assertThat(exception.getMessage()).isEqualTo("Order không tồn tại với id: " + fakeOrderId);
        verify(orderRepository, never()).save(any());
        verify(orderDetailRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test 2 [AddItems]: Fails_When_OrderNotPending")
    void addItems_Fails_When_OrderNotPending() {
        // 1. GIVEN
        mockOrder.setStatus("COMPLETED"); // Set trạng thái đã hoàn thành
        when(orderRepository.findById(fakeOrderId)).thenReturn(Optional.of(mockOrder));

        // 2. WHEN & 3. THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.addItemsToOrder(fakeOrderId, addItemsRequest, authentication);
        });

        assertThat(exception.getMessage()).isEqualTo("Chỉ có thể thêm món vào order đang ở trạng thái PENDING");
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test 3 [AddItems]: Fails_When_MenuItemNotFound")
    void addItems_Fails_When_MenuItemNotFound() {
        // 1. GIVEN
        Long invalidMenuItemId = 99L;
        AddItemsRequest.OrderDetailItem itemRequest = new AddItemsRequest.OrderDetailItem(invalidMenuItemId, 1, null);
        addItemsRequest.setItems(List.of(itemRequest));

        when(orderRepository.findById(fakeOrderId)).thenReturn(Optional.of(mockOrder));
        when(menuItemRepository.findById(invalidMenuItemId)).thenReturn(Optional.empty()); // Không tìm thấy món

        // 2. WHEN & 3. THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.addItemsToOrder(fakeOrderId, addItemsRequest, authentication);
        });

        assertThat(exception.getMessage()).isEqualTo("MenuItem không tồn tại với id: " + invalidMenuItemId);
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test 4 [AddItems]: Fails_When_MenuItemNotAvailable")
    void addItems_Fails_When_MenuItemNotAvailable() {
        // 1. GIVEN
        mockMenuItem1.setStatus("Unavailable"); // Món không có sẵn
        AddItemsRequest.OrderDetailItem itemRequest = new AddItemsRequest.OrderDetailItem(mockMenuItem1.getId(), 1, null);
        addItemsRequest.setItems(List.of(itemRequest));

        when(orderRepository.findById(fakeOrderId)).thenReturn(Optional.of(mockOrder));
        when(menuItemRepository.findById(mockMenuItem1.getId())).thenReturn(Optional.of(mockMenuItem1));

        // 2. WHEN & 3. THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.addItemsToOrder(fakeOrderId, addItemsRequest, authentication);
        });

        assertThat(exception.getMessage()).isEqualTo("Món " + mockMenuItem1.getName() + " hiện không có sẵn");
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test 5 [AddItems]: Success_AddSingleItem")
    void addItems_Success_AddSingleItem() {
        // 1. GIVEN
        // Thêm 2 Phở Bò (2 * 50 = 100)
        AddItemsRequest.OrderDetailItem item1 = new AddItemsRequest.OrderDetailItem(mockMenuItem1.getId(), 2, "nhiều bánh");
        addItemsRequest.setItems(List.of(item1));

        when(orderRepository.findById(fakeOrderId)).thenReturn(Optional.of(mockOrder));
        when(menuItemRepository.findById(mockMenuItem1.getId())).thenReturn(Optional.of(mockMenuItem1));

        // Mock các hàm save
        when(orderDetailRepository.save(any(OrderDetail.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        when(orderMapper.toResponse(mockOrder)).thenReturn(mockResponse); // Giả sử dùng chung mockResponse

        // 2. WHEN
        OrderResponse response = orderService.addItemsToOrder(fakeOrderId, addItemsRequest, authentication);

        // 3. THEN
        assertThat(response).isNotNull();

        // 3.1. Kiểm tra OrderDetail đã được save 1 lần
        verify(orderDetailRepository, times(1)).save(orderDetailCaptor.capture());
        OrderDetail capturedDetail = orderDetailCaptor.getValue();

        assertThat(capturedDetail.getQuantity()).isEqualTo(2);
        assertThat(capturedDetail.getPriceAtOrder()).isEqualTo(new BigDecimal("50.00"));
        assertThat(capturedDetail.getNotes()).isEqualTo("nhiều bánh");

        // 3.2. Kiểm tra Order tổng
        verify(orderRepository, times(1)).save(orderArgumentCaptor.capture());
        Order capturedOrder = orderArgumentCaptor.getValue();

        // Tổng tiền = (2 * 50) = 100
        assertThat(capturedOrder.getTotalAmount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(capturedOrder.getUpdatedAt()).isEqualTo(fakeNow); // Kiểm tra thời gian
        assertThat(capturedOrder.getOrderDetails()).hasSize(1);
    }

    @Test
    @DisplayName("Test 6 [AddItems]: Success_AddMultipleItems")
    void addItems_Success_AddMultipleItems() {
        // 1. GIVEN
        // Món 1: 2 Phở Bò (2 * 50 = 100)
        AddItemsRequest.OrderDetailItem item1 = new AddItemsRequest.OrderDetailItem(mockMenuItem1.getId(), 2, null);
        // Món 2: 3 Coca (3 * 10 = 30)
        AddItemsRequest.OrderDetailItem item2 = new AddItemsRequest.OrderDetailItem(mockMenuItem2.getId(), 3, null);
        addItemsRequest.setItems(List.of(item1, item2));

        when(orderRepository.findById(fakeOrderId)).thenReturn(Optional.of(mockOrder));
        when(menuItemRepository.findById(mockMenuItem1.getId())).thenReturn(Optional.of(mockMenuItem1));
        when(menuItemRepository.findById(mockMenuItem2.getId())).thenReturn(Optional.of(mockMenuItem2));

        when(orderDetailRepository.save(any(OrderDetail.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenReturn(mockOrder);
        when(orderMapper.toResponse(mockOrder)).thenReturn(mockResponse);

        // 2. WHEN
        orderService.addItemsToOrder(fakeOrderId, addItemsRequest, authentication);

        // 3. THEN
        // 3.1. Kiểm tra OrderDetail đã được save 2 lần
        verify(orderDetailRepository, times(2)).save(any(OrderDetail.class));

        // 3.2. Kiểm tra Order tổng
        verify(orderRepository, times(1)).save(orderArgumentCaptor.capture());
        Order capturedOrder = orderArgumentCaptor.getValue();

        // Tổng tiền = (2 * 50) + (3 * 10) = 100 + 30 = 130
        assertThat(capturedOrder.getTotalAmount()).isEqualByComparingTo(new BigDecimal("130.00"));
        assertThat(capturedOrder.getUpdatedAt()).isEqualTo(fakeNow);
        assertThat(capturedOrder.getOrderDetails()).hasSize(2); // Order đã chứa 2 món mới
    }
}