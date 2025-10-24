package com.example.restaurant_management.service;

import com.example.restaurant_management.entity.OrderSession;
import com.example.restaurant_management.entity.TableEntity;
import com.example.restaurant_management.entity.User; // Giả sử entity của bạn là User
import com.example.restaurant_management.repository.OrderSessionRepository;
import com.example.restaurant_management.repository.TableRepository;
import com.example.restaurant_management.repository.UserRepository; // Giả sử bạn có
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class OrderSessionService {

    private final TableRepository tableRepository;
    private final OrderSessionRepository orderSessionRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrderSessionService(TableRepository tableRepository,
                               OrderSessionRepository orderSessionRepository,
                               UserRepository userRepository) {
        this.tableRepository = tableRepository;
        this.orderSessionRepository = orderSessionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Đây là hàm xử lý logic "Check-in" (Tạo mới hoặc Tham gia)
     * @Transactional đảm bảo tất cả thao tác (tạo session, cập nhật bàn)
     * hoặc cùng thành công, hoặc cùng thất bại. Rất quan trọng!
     */
    @Transactional
    public TableEntity startOrJoinSession(Long tableId, Long userId) {

        // 1. Lấy thông tin Bàn và User
        // (findByUsername(username) là giả định, bạn thay bằng hàm của bạn)
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user: " + userId));

        TableEntity table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn: " + tableId));

        // 2. Lấy phiên (session) HIỆN TẠI của bàn
        OrderSession existingSession = table.getCurrentOrderSession();

        // 3. XỬ LÝ NGHIỆP VỤ:

        // === KỊCH BẢN 1: BÀN ĐÃ CÓ KHÁCH (kịch bản Vợ-Chồng) ===
        if (existingSession != null && "ACTIVE".equals(existingSession.getStatus())) {

            System.out.println("User [" + userId + "] tham gia session ["
                    + existingSession.getId() + "] tại bàn [" + table.getTableNumber() + "]");

            // Không cần làm gì thêm!
            // Chỉ cần trả về thông tin Bàn, React app sẽ tự động
            // dùng session ID đó để order.
            return table;
        }

        // === KỊCH BẢN 2: BÀN TRỐNG (hoặc session cũ đã 'COMPLETED') ===
        // (Đây là ông Chồng, người quét đầu tiên)
        System.out.println("User [" + userId + "] BẮT ĐẦU session mới tại bàn ["
                + table.getTableNumber() + "]");

        // a. Tạo một OrderSession (Hóa đơn tổng) mới
        OrderSession newSession = new OrderSession();
        newSession.setTable(table);
        newSession.setCustomerUser(currentUser); // Gán người quét đầu tiên làm "chủ xị"
        newSession.setStatus("ACTIVE");
        newSession.setTotalAmount(BigDecimal.ZERO); // Khởi tạo tổng tiền
        // setCreatedAt tự động (nếu bạn dùng @CreationTimestamp)

        OrderSession savedSession = orderSessionRepository.save(newSession);

        // b. "KHÓA" BÀN: Gán session mới này vào Bàn
        // Đây là "chìa khóa" của toàn bộ hệ thống!
        table.setCurrentOrderSession(savedSession);
        table.setStatus("OCCUPIED"); // Chuyển trạng thái bàn

        TableEntity updatedTable = tableRepository.save(table);

        return updatedTable;
    }
}