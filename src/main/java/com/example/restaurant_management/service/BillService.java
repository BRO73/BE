package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.PaymentRequest;
import com.example.restaurant_management.dto.request.SplitBillRequest;
import com.example.restaurant_management.dto.request.SplitItemRequest;
import com.example.restaurant_management.dto.response.BillDTO;
import com.example.restaurant_management.dto.response.PendingBillDTO;
import com.example.restaurant_management.entity.*;
import com.example.restaurant_management.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillService {

    private final OrderDetailRepository orderDetailRepo;
    private final OrderSessionRepository orderSessionRepo;
    private final BillRepository billRepo;
    private final BillItemRepository billItemRepo;
    private final TransactionRepository transactionRepo;
    private final MenuItemRepository menuItemRepo;

    // Tolerance để so sánh BigDecimal (0.01 VNĐ)
    private static final BigDecimal TOLERANCE = new BigDecimal("0.01");
    private static final int SCALE = 2;

    /**
     * 1. HÀM XEM NỢ (VIEW PENDING BILL)
     * Hiển thị các món còn nợ của một session
     */
    public List<PendingBillDTO> viewPendingBill(Long sessionId) {
        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID cannot be null");
        }

        List<OrderDetail> allDetails = orderDetailRepo.findByOrderOrderSessionId(sessionId);

        if (allDetails.isEmpty()) {
            return List.of(); // Không có món nào
        }

        // Gộp các món nợ theo MenuItem
        Map<MenuItem, BigDecimal> unpaidMap = allDetails.stream()
                .collect(Collectors.groupingBy(
                        OrderDetail::getMenuItem,
                        Collectors.mapping(
                                od -> calculateUnpaidAmount(od),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        // Convert sang DTO
        return unpaidMap.entrySet().stream()
                .filter(entry -> entry.getValue().compareTo(TOLERANCE) > 0) // Chỉ lấy món còn nợ > 0.01
                .map(entry -> {
                    MenuItem item = entry.getKey();
                    BigDecimal amountUnpaid = entry.getValue().setScale(SCALE, RoundingMode.HALF_UP);

                    // Tính số lượng lẻ dựa trên giá hiện tại
                    BigDecimal price = item.getPrice();
                    if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                        log.warn("Menu item {} has invalid price, using 1.0 for calculation", item.getId());
                        price = BigDecimal.ONE;
                    }

                    BigDecimal quantityUnpaid = amountUnpaid.divide(price, SCALE, RoundingMode.HALF_UP);

                    return new PendingBillDTO(item.getId(), item.getName(), quantityUnpaid, amountUnpaid);
                })
                .collect(Collectors.toList());
    }

    /**
     * 2. HÀM TÁCH BILL (CREATE SPLIT BILL)
     * Tạo bill mới từ một số món trong session
     */
    @Transactional
    public BillDTO createSplitBill(SplitBillRequest request) {
        // Validate request
        validateSplitBillRequest(request);

        // Lấy session và kiểm tra status
        OrderSession session = orderSessionRepo.findById(request.sessionId())
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + request.sessionId()));

        if ("CLOSED".equals(session.getStatus())) {
            throw new IllegalStateException("Cannot create bill for closed session: " + session.getId());
        }

        // Tạo bill TRƯỚC để có ID (quan trọng!)
        Bill bill = billRepo.save(Bill.builder()
                .originatingSession(session)
                .status("PENDING")
                .totalAmount(BigDecimal.ZERO)
                .build());

        BigDecimal totalBillAmount = BigDecimal.ZERO;

        // Phân bổ từng món vào bill
        for (SplitItemRequest item : request.items()) {
            validateSplitItem(item);

            // Lấy menu item để có giá
            MenuItem menuItem = menuItemRepo.findById(item.menuItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + item.menuItemId()));

            BigDecimal priceAtOrder = menuItem.getPrice();
            if (priceAtOrder == null || priceAtOrder.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalStateException("Menu item " + menuItem.getId() + " has invalid price");
            }

            BigDecimal amountToAllocate = priceAtOrder
                    .multiply(BigDecimal.valueOf(item.quantity()))
                    .setScale(SCALE, RoundingMode.HALF_UP);

            // Phân bổ món vào bill
            assignItemsToBill(session.getId(), item.menuItemId(), amountToAllocate, bill);
            totalBillAmount = totalBillAmount.add(amountToAllocate);
        }

        // Cập nhật tổng tiền
        bill.setTotalAmount(totalBillAmount.setScale(SCALE, RoundingMode.HALF_UP));
        billRepo.save(bill);

        log.info("Created split bill {} with total amount {}", bill.getId(), bill.getTotalAmount());

        return new BillDTO(bill.getId(), bill.getTotalAmount(), bill.getStatus());
    }

    /**
     * 3. HÀM PHÂN BỔ MÓN VÀO BILL (ASSIGN ITEMS)
     * Phân bổ số tiền cần trả vào các OrderDetail theo FIFO
     */
    @Transactional
    protected void assignItemsToBill(Long sessionId, Long menuItemId, BigDecimal amountToAllocate, Bill bill) {
        // Lấy danh sách OrderDetail chưa trả đủ, sắp xếp theo thứ tự (FIFO)
        List<OrderDetail> candidates = orderDetailRepo.findUnpaidOrderDetails(sessionId, menuItemId);

        if (candidates.isEmpty()) {
            throw new IllegalStateException(
                    String.format("No unpaid items found for menu item %d in session %d", menuItemId, sessionId));
        }

        BigDecimal remaining = amountToAllocate;

        for (OrderDetail od : candidates) {
            if (remaining.compareTo(TOLERANCE) <= 0) {
                break; // Đã phân bổ đủ
            }

            BigDecimal totalOwed = od.getPriceAtOrder()
                    .multiply(BigDecimal.valueOf(od.getQuantity()))
                    .setScale(SCALE, RoundingMode.HALF_UP);

            BigDecimal amountUnpaidOnOD = totalOwed.subtract(od.getAmountPaid())
                    .setScale(SCALE, RoundingMode.HALF_UP);

            // Bỏ qua nếu OrderDetail này đã trả hết
            if (amountUnpaidOnOD.compareTo(TOLERANCE) <= 0) {
                continue;
            }

            // Số tiền thực tế phân bổ cho OrderDetail này
            BigDecimal amountToPayForThisOD = remaining.min(amountUnpaidOnOD)
                    .setScale(SCALE, RoundingMode.HALF_UP);

            // Tạo BillItem
            BillItem billItem = BillItem.builder()
                    .bill(bill)
                    .originalOrderDetail(od)
                    .amount(amountToPayForThisOD)
                    .build();
            billItemRepo.save(billItem);

            remaining = remaining.subtract(amountToPayForThisOD);

            log.debug("Allocated {} to OrderDetail {}, remaining: {}",
                    amountToPayForThisOD, od.getId(), remaining);
        }

        // Kiểm tra xem có phân bổ đủ không
        if (remaining.compareTo(TOLERANCE) > 0) {
            throw new IllegalStateException(
                    String.format("Not enough unpaid items for menu item %d. Over allocated by %s",
                            menuItemId, remaining));
        }
    }

    /**
     * 4. HÀM THANH TOÁN BILL (PAY BILL)
     * Thanh toán bill và cập nhật sổ đối soát
     */
    @Transactional
    public BillDTO payBill(Long billId, PaymentRequest request) {
        if (billId == null) {
            throw new IllegalArgumentException("Bill ID cannot be null");
        }
        if (request == null || request.paymentMethod() == null || request.paymentMethod().isBlank()) {
            throw new IllegalArgumentException("Payment method is required");
        }

        Bill bill = billRepo.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found: " + billId));

        if ("PAID".equals(bill.getStatus())) {
            throw new IllegalStateException("Bill already paid: " + billId);
        }

        // Kiểm tra xem có transaction nào rồi không (tránh duplicate)
        if (transactionRepo.existsByBill(bill)) {
            throw new IllegalStateException("Bill " + billId + " already has a transaction");
        }

        // 1. Tạo giao dịch
        Transaction transaction = Transaction.builder()
                .bill(bill)
                .amountPaid(bill.getTotalAmount())
                .paymentMethod(request.paymentMethod())
                .transactionTime(LocalDateTime.now())
                .build();
        transactionRepo.save(transaction);

        // 2. Cập nhật status Bill
        bill.setStatus("PAID");
        billRepo.save(bill);

        // 3. CẬP NHẬT SỔ ĐỐI SOÁT (Critical!)
        List<BillItem> items = billItemRepo.findByBill(bill);
        for (BillItem item : items) {
            OrderDetail od = item.getOriginalOrderDetail();
            BigDecimal newAmountPaid = od.getAmountPaid().add(item.getAmount())
                    .setScale(SCALE, RoundingMode.HALF_UP);
            od.setAmountPaid(newAmountPaid);
            orderDetailRepo.save(od);

            log.debug("Updated OrderDetail {} amountPaid to {}", od.getId(), newAmountPaid);
        }

        // 4. Kiểm tra có thể đóng session không
        checkAndCloseSession(bill.getOriginatingSession());

        log.info("Paid bill {} with amount {} via {}",
                bill.getId(), bill.getTotalAmount(), request.paymentMethod());

        return new BillDTO(bill.getId(), bill.getTotalAmount(), bill.getStatus());
    }

    /**
     * 5. HELPER - KIỂM TRA VÀ ĐÓNG SESSION
     * Tự động đóng session nếu đã trả hết nợ
     */
    @Transactional
    protected void checkAndCloseSession(OrderSession session) {
        // Tải lại session để có dữ liệu mới nhất
        OrderSession freshSession = orderSessionRepo.findById(session.getId())
                .orElseThrow(() -> new IllegalStateException("Session not found: " + session.getId()));

        // Nếu đã đóng rồi thì bỏ qua
        if ("CLOSED".equals(freshSession.getStatus())) {
            return;
        }

        List<OrderDetail> allDetails = orderDetailRepo.findByOrderOrderSessionId(freshSession.getId());

        if (allDetails.isEmpty()) {
            return; // Không có order nào, không cần đóng
        }

        BigDecimal totalOwed = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;

        for (OrderDetail od : allDetails) {
            BigDecimal itemTotal = od.getPriceAtOrder()
                    .multiply(BigDecimal.valueOf(od.getQuantity()))
                    .setScale(SCALE, RoundingMode.HALF_UP);
            totalOwed = totalOwed.add(itemTotal);
            totalPaid = totalPaid.add(od.getAmountPaid());
        }

        // So sánh với tolerance
        BigDecimal difference = totalPaid.subtract(totalOwed).abs();
        if (difference.compareTo(TOLERANCE) <= 0 || totalPaid.compareTo(totalOwed) >= 0) {
            freshSession.setStatus("CLOSED");
            orderSessionRepo.save(freshSession);
            log.info("Closed session {} - Total owed: {}, Total paid: {}",
                    freshSession.getId(), totalOwed, totalPaid);
        }
    }

    // ==================== PRIVATE HELPERS ====================

    private BigDecimal calculateUnpaidAmount(OrderDetail od) {
        BigDecimal total = od.getPriceAtOrder()
                .multiply(BigDecimal.valueOf(od.getQuantity()))
                .setScale(SCALE, RoundingMode.HALF_UP);
        return total.subtract(od.getAmountPaid());
    }

    private void validateSplitBillRequest(SplitBillRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Split bill request cannot be null");
        }
        if (request.sessionId() == null) {
            throw new IllegalArgumentException("Session ID cannot be null");
        }
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("Bill items cannot be empty");
        }
    }

    private void validateSplitItem(SplitItemRequest item) {
        if (item == null) {
            throw new IllegalArgumentException("Split item cannot be null");
        }
        if (item.menuItemId() == null) {
            throw new IllegalArgumentException("Menu item ID cannot be null");
        }
        // Bỏ check null vì double là primitive type, không bao giờ null
        if (item.quantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive: " + item.quantity());
        }
    }
}