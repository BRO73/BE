package com.example.restaurant_management;

import com.example.restaurant_management.dto.request.MenuItemRequest;
import com.example.restaurant_management.entity.MenuItem;
import com.example.restaurant_management.mapper.MenuItemMapper;
import com.example.restaurant_management.repository.CategoryRepository;
import com.example.restaurant_management.repository.MenuItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// ==================== EXCEPTION CLASSES ====================

class ResourceNotFoundException extends RuntimeException {
    ResourceNotFoundException(String message) { super(message); }
}

class DuplicateMenuItemException extends RuntimeException {
    DuplicateMenuItemException(String message) { super(message); }
}

// ==================== SERVICE IMPL (TRONG CÙNG FILE) ====================

class MenuItemServiceImpl {
    private final MenuItemRepository menuItemRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemMapper menuItemMapper;

    MenuItemServiceImpl(MenuItemRepository menuItemRepository,
                        CategoryRepository categoryRepository,
                        MenuItemMapper menuItemMapper) {
        this.menuItemRepository = menuItemRepository;
        this.categoryRepository = categoryRepository;
        this.menuItemMapper = menuItemMapper;
    }

    MenuItem createMenuItem(MenuItemRequest request) {
        // 1. Validate Category
        if (request.getCategoryName() == null || !categoryRepository.existsByName(request.getCategoryName())) {
            throw new ResourceNotFoundException("Category not found: " + request.getCategoryName());
        }

        // 2. Validate Name
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Menu item name is required");
        }
        if (request.getName().length() > 100) {
            throw new IllegalArgumentException("Menu item name must not exceed 100 characters");
        }

        // 3. Validate Price
        if (request.getPrice() == null) {
            throw new IllegalArgumentException("Price is required");
        }
        if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }

        // 4. Check Duplicate
        List<MenuItem> duplicates = menuItemRepository.findByNameContainingIgnoreCase(request.getName());
        if (!duplicates.isEmpty()) {
            throw new DuplicateMenuItemException("Menu item's name already exists: " + request.getName());
        }

        // 5. Map & Save
        MenuItem menuItem = menuItemMapper.toEntity(request);
        if (menuItem == null) {
            throw new IllegalStateException("Mapper returned null");
        }

        return menuItemRepository.save(menuItem);
    }
}

// ==================== UNIT TEST CLASS ====================

@DisplayName("MenuItemServiceImpl - Unit Test")
class MenuItemServiceImplTest {

    @Mock private MenuItemRepository menuItemRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private MenuItemMapper menuItemMapper;

    @InjectMocks private MenuItemServiceImpl menuItemService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Thành công → Trả về MenuItem")
    void testCreateMenuItem_ValidData() {
        MenuItemRequest request = MenuItemRequest.builder()
                .categoryName("Main Course")
                .name("Beef Pho")
                .price(BigDecimal.valueOf(75000))
                .status("ACTIVE")
                .build();

        MenuItem entity = MenuItem.builder()
                .name("Beef Pho")
                .price(BigDecimal.valueOf(75000))
                .status("ACTIVE")
                .build();

        when(categoryRepository.existsByName("Main Course")).thenReturn(true);
        when(menuItemRepository.findByNameContainingIgnoreCase("Beef Pho")).thenReturn(Collections.emptyList());
        when(menuItemMapper.toEntity(request)).thenReturn(entity);
        when(menuItemRepository.save(entity)).thenReturn(entity);

        MenuItem result = menuItemService.createMenuItem(request);

        assertNotNull(result);
        assertEquals("Beef Pho", result.getName());
        verify(menuItemRepository).save(entity);
    }

    @Test
    @DisplayName("Tên rỗng → throw IllegalArgumentException")
    void testCreateMenuItem_EmptyName() {
        MenuItemRequest request = MenuItemRequest.builder()
                .categoryName("Main Course")
                .name("")
                .price(BigDecimal.valueOf(75000))
                .status("ACTIVE")
                .build();

        // PHẢI MOCK CATEGORY TỒN TẠI
        when(categoryRepository.existsByName("Main Course")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                menuItemService.createMenuItem(request));

        assertTrue(ex.getMessage().contains("name is required"));
        verify(menuItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Giá âm → throw IllegalArgumentException")
    void testCreateMenuItem_NegativePrice() {
        MenuItemRequest request = MenuItemRequest.builder()
                .categoryName("Main Course")
                .name("Beef Pho")
                .price(BigDecimal.valueOf(-1))
                .status("ACTIVE")
                .build();

        when(categoryRepository.existsByName("Main Course")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                menuItemService.createMenuItem(request));

        assertTrue(ex.getMessage().contains("greater than 0"));
        verify(menuItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Danh mục không tồn tại → throw ResourceNotFoundException")
    void testCreateMenuItem_CategoryNotFound() {
        MenuItemRequest request = MenuItemRequest.builder()
                .categoryName("XYZ")
                .name("Beef Pho")
                .price(BigDecimal.valueOf(75000))
                .status("ACTIVE")
                .build();

        when(categoryRepository.existsByName("XYZ")).thenReturn(false);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                menuItemService.createMenuItem(request));

        assertTrue(ex.getMessage().contains("Category not found"));
        verify(menuItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Thiếu tên → throw IllegalArgumentException")
    void testCreateMenuItem_MissingName() {
        MenuItemRequest request = MenuItemRequest.builder()
                .categoryName("Main Course")
                .price(BigDecimal.valueOf(75000))
                .status("ACTIVE")
                .build();

        when(categoryRepository.existsByName("Main Course")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                menuItemService.createMenuItem(request));

        assertTrue(ex.getMessage().contains("name is required"));
        verify(menuItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Tên quá dài → throw IllegalArgumentException")
    void testCreateMenuItem_NameTooLong() {
        String longName = "A".repeat(101);
        MenuItemRequest request = MenuItemRequest.builder()
                .categoryName("Main Course")
                .name(longName)
                .price(BigDecimal.valueOf(75000))
                .status("ACTIVE")
                .build();

        when(categoryRepository.existsByName("Main Course")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                menuItemService.createMenuItem(request));

        assertTrue(ex.getMessage().contains("must not exceed 100"));
        verify(menuItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Giá = 0 → throw IllegalArgumentException")
    void testCreateMenuItem_PriceZero() {
        MenuItemRequest request = MenuItemRequest.builder()
                .categoryName("Main Course")
                .name("Beef Pho")
                .price(BigDecimal.ZERO)
                .status("ACTIVE")
                .build();

        when(categoryRepository.existsByName("Main Course")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                menuItemService.createMenuItem(request));

        assertTrue(ex.getMessage().contains("greater than 0"));
        verify(menuItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("Trùng tên → throw DuplicateMenuItemException")
    void testCreateMenuItem_DuplicateName() {
        MenuItemRequest request = MenuItemRequest.builder()
                .categoryName("Main Course")
                .name("Beef Pho")
                .price(BigDecimal.valueOf(75000))
                .status("ACTIVE")
                .build();

        when(categoryRepository.existsByName("Main Course")).thenReturn(true);
        when(menuItemRepository.findByNameContainingIgnoreCase("Beef Pho"))
                .thenReturn(List.of(MenuItem.builder().name("Beef Pho").build()));

        DuplicateMenuItemException ex = assertThrows(DuplicateMenuItemException.class, () ->
                menuItemService.createMenuItem(request));

        assertTrue(ex.getMessage().contains("already exists"));
        verify(menuItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("CategoryName là số → Không hợp lệ")
    void testCreateMenuItem_CategoryNameNumber() {
        MenuItemRequest request = MenuItemRequest.builder()
                .categoryName("123")
                .name("Beef Pho")
                .price(BigDecimal.valueOf(75000))
                .status("ACTIVE")
                .build();

        MenuItem entity = MenuItem.builder()
                .name("Beef Pho")
                .price(BigDecimal.valueOf(75000))
                .status("ACTIVE")
                .build();

        when(categoryRepository.existsByName("123")).thenReturn(true);
        when(menuItemRepository.findByNameContainingIgnoreCase("Beef Pho")).thenReturn(Collections.emptyList());
        when(menuItemMapper.toEntity(request)).thenReturn(entity);
        when(menuItemRepository.save(entity)).thenReturn(entity);

        MenuItem result = menuItemService.createMenuItem(request);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Price null → throw IllegalArgumentException")
    void testCreateMenuItem_PriceNull() {
        MenuItemRequest request = MenuItemRequest.builder()
                .categoryName("Main Course")
                .name("Beef Pho")
                .price(null)
                .status("ACTIVE")
                .build();

        when(categoryRepository.existsByName("Main Course")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                menuItemService.createMenuItem(request));

        assertTrue(ex.getMessage().contains("Price is required"));
        verify(menuItemRepository, never()).save(any());
    }
}