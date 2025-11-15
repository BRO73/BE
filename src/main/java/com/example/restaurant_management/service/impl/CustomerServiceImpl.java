package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.request.CustomerRequest;
import com.example.restaurant_management.dto.response.CustomerResponse;
import com.example.restaurant_management.entity.Customer;
import com.example.restaurant_management.entity.Role;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.entity.UserRole;
import com.example.restaurant_management.repository.CustomerRepository;
import com.example.restaurant_management.repository.RoleRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.repository.UserRoleRepository;
import com.example.restaurant_management.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
        return mapToResponse(customer);
    }

    @Override
    public List<CustomerResponse> searchCustomers(String keyword) {
        // Nếu keyword trống/null thì trả về toàn bộ
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCustomers();
        }

        return customerRepository.searchCustomers(keyword.trim())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse findByPhoneNumber(String phoneNumber) {
        Customer customer = customerRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NoSuchElementException("Customer with phone number " + phoneNumber + " not found"));
        return mapToResponse(customer);
    }



    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));

        // ✅ Xóa User để cascade xóa Customer theo quan hệ trong DB
        User user = customer.getUser();
        if (user != null) {
            userRepository.delete(user);
        } else {
            customerRepository.delete(customer);
        }
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (request.getFullName() != null)
            customer.setFullName(request.getFullName());

        if (request.getEmail() != null)
            customer.setEmail(request.getEmail());

        if (request.getPhone() != null)
            customer.setPhoneNumber(request.getPhone());

        if (request.getActivated() != null)
            customer.setActivated(request.getActivated());

        customerRepository.save(customer);

        return mapToResponse(customer);
    }


    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .build();
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return customerRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    @Transactional
    public CustomerResponse findOrCreateCustomer(CustomerRequest request) {
        String phoneNumber = request.getPhone(); // Giả sử request DTO dùng getPhone()

        // 1. Thử tìm khách hàng bằng SĐT
        Optional<Customer> existingCustomer = customerRepository.findByPhoneNumber(phoneNumber);

        if (existingCustomer.isPresent()) {
            Customer customer = existingCustomer.get();
            User user = customer.getUser(); // Lấy user liên kết

            return CustomerResponse.builder()
                    .id(customer.getId())
                    .userId(user != null ? user.getId() : null) // Trả về userId nếu có
                    .phoneNumber(customer.getPhoneNumber())
                    .fullName(customer.getFullName())
                    .email(customer.getEmail())
                    .build();
        }

        // 2. Nếu không tìm thấy, tạo mới Customer
        Customer newCustomer = new Customer();
        newCustomer.setPhoneNumber(phoneNumber);

        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            newCustomer.setFullName(request.getFullName());
        } else {
            newCustomer.setFullName(phoneNumber); // Hoặc "Khách vãng lai"
        }
        newCustomer.setActivated(true);
        // Ghi chú: chưa save vội, đợi tạo User

        // 3. Tạo User mới
        User user = User.builder()
                .username(newCustomer.getPhoneNumber()) // Dùng SĐT làm username
                .build();
        // Ghi chú: không set customer cho user, vì Customer là bên sở hữu quan hệ

        // 4. Liên kết 2 chiều
        newCustomer.setUser(user); // Customer trỏ đến User
        // User không cần trỏ ngược lại Customer (vì Customer là @JoinColumn)

        // 5. Lưu User trước (vì Customer tham chiếu đến User)
        // (Điều này tùy thuộc vào cấu hình 'cascade' của bạn, nhưng lưu User trước là an toàn)
        userRepository.save(user);

        // 6. Lưu Customer
        customerRepository.save(newCustomer);

        // 7. Gán Role "CUSTOMER"
        Role role = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER not found."));

        UserRole userRole = UserRole.builder()
                .roleId(role.getId())
                .userId(user.getId())
                .build();
        userRoleRepository.save(userRole);

        // 8. Trả về Response
        return CustomerResponse.builder()
                .id(newCustomer.getId())
                .userId(user.getId()) // << TRẢ VỀ USER ID
                .phoneNumber(newCustomer.getPhoneNumber())
                .fullName(newCustomer.getFullName())
                .email(newCustomer.getEmail())
                .build();
    }
}
