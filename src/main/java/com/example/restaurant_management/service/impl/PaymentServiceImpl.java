package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.exception.RestaurantException;
import com.example.restaurant_management.dto.request.CashPaymentRequest;
import com.example.restaurant_management.dto.request.PaymentRequestDTO;
import com.example.restaurant_management.dto.response.CashPaymentResponse;
import com.example.restaurant_management.dto.response.PaymentResponseDTO;
import com.example.restaurant_management.entity.Order;
import com.example.restaurant_management.entity.OrderDetail;
import com.example.restaurant_management.entity.Promotion;
import com.example.restaurant_management.entity.Transaction;
import com.example.restaurant_management.repository.OrderRepository;
import com.example.restaurant_management.repository.PromotionRepository;
import com.example.restaurant_management.repository.TransactionRepository;
import com.example.restaurant_management.service.PaymentService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.payos.PayOS;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkRequest;
import vn.payos.model.v2.paymentRequests.CreatePaymentLinkResponse;
import vn.payos.model.v2.paymentRequests.PaymentLinkItem;
import vn.payos.model.webhooks.WebhookData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PayOS payOS;
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;
    private final PromotionRepository promotionRepository;

    @Override
    @Transactional
    public PaymentResponseDTO createPaymentLink(PaymentRequestDTO request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RestaurantException("Order not found"));

        Map<String, AggregatedItem> aggregatedItemMap = new HashMap<>();

        for (OrderDetail detail : order.getOrderDetails()) {
            if (detail.getStatus().equals("PENDING")
                    || detail.getStatus().equals("DONE")
                    || detail.getStatus().equals("COMPLETED")
                    || detail.getStatus().equals("IN_PROGRESS")) {
                String menuItemName = detail.getMenuItem().getName();
                Long itemPrice = detail.getPriceAtOrder().longValue();
                String key = menuItemName + "::" + itemPrice;

                aggregatedItemMap.merge(
                        key,
                        new AggregatedItem(menuItemName, itemPrice, detail.getQuantity()),
                        (existing, newItem) -> {
                            existing.setQuantity(existing.getQuantity() + newItem.getQuantity());
                            return existing;
                        }
                );
            }
        }

        List<PaymentLinkItem> items = new ArrayList<>();
        aggregatedItemMap.forEach((key, aggItem) -> {
            PaymentLinkItem item = PaymentLinkItem.builder()
                    .name(aggItem.getName())
                    .quantity(aggItem.getQuantity())
                    .price(aggItem.getPrice())
                    .build();
            items.add(item);
        });

        Long amountOriginal = 0L;
        for (PaymentLinkItem item : items) {
            if (item.getPrice() > 0) {
                amountOriginal += item.getPrice() * item.getQuantity();
            }
        }

        Long amountPaid = amountOriginal;
        Long discountAmount = 0L;
        Long orderCode = System.currentTimeMillis() / 1000;

        Promotion promotion = null;
        if (request.getPromotionCode() != null && !request.getPromotionCode().isEmpty()) {
            promotion = promotionRepository.findByCode(request.getPromotionCode())
                    .orElse(null);
        }

        if (promotion != null) {
            if (promotion.getEndDate().isBefore(LocalDateTime.now())){
                log.warn("Promotion code {} has no uses left.", request.getPromotionCode());
                promotion = null;
            }
        }

        if (promotion != null) {
            if(promotion.getPromotionType().equalsIgnoreCase("percentage")) {
                discountAmount = amountOriginal * promotion.getValue().longValue() / 100;
            } else if(promotion.getPromotionType().equalsIgnoreCase("fixed")) {
                discountAmount = promotion.getValue().longValue();
            }
        }

        if (discountAmount > 0) {
            PaymentLinkItem discountItem = PaymentLinkItem.builder()
                    .name("Giảm giá")
                    .quantity(1)
                    .price(-discountAmount)
                    .build();
            items.add(discountItem);
        }

        amountPaid = amountOriginal - discountAmount;

        amountPaid = Math.max(1000L, amountPaid);

        List<PaymentLinkItem> testItems = new ArrayList<>();
        for (PaymentLinkItem item : items) {
            testItems.add(PaymentLinkItem.builder()
                    .name(item.getName())
                    .quantity(item.getQuantity())
                    .price(item.getPrice() / 10)
                    .build());
        }

        Long testAmountPaid = amountPaid / 10;

        testAmountPaid = Math.max(1000L, testAmountPaid);

        List<PaymentLinkItem> finalItems;
        if (testAmountPaid.equals(1000L) && (amountPaid / 10) < 1000L) {
            finalItems = new ArrayList<>();
        } else {
            finalItems = testItems;
        }

        CreatePaymentLinkRequest paymentRequest = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .description(String.format("Pay order #%d", order.getId()))
                .amount(testAmountPaid)
                .items(finalItems)
                .returnUrl(request.getReturnUrl())
                .cancelUrl(request.getCancelUrl())
                .build();

        try {
            CreatePaymentLinkResponse payosResponse = payOS.paymentRequests().create(paymentRequest);

            Transaction transaction = Transaction.builder()
                    .order(order)
                    .amountPaid(BigDecimal.valueOf(testAmountPaid))
                    .amountOriginal(BigDecimal.valueOf(amountOriginal))
                    .discountAmount(BigDecimal.valueOf(discountAmount))
                    .paymentMethod("PAYOS")
                    .transactionTime(LocalDateTime.now())
                    .transactionCode(orderCode.toString())
                    .paymentStatus("PENDING")
                    .paymentLinkId(payosResponse.getPaymentLinkId())
                    .checkoutUrl(payosResponse.getCheckoutUrl())
                    .build();

            if (promotion != null) {
                transaction.setPromotionValue(promotion.getValue());
                transaction.setPromotionType(promotion.getPromotionType());
                transaction.setPromotionId(promotion.getId());
            }

            transaction = transactionRepository.save(transaction);

            return PaymentResponseDTO.builder()
                    .transactionCode(transaction.getTransactionCode())
                    .checkoutUrl(transaction.getCheckoutUrl())
                    .paymentStatus(transaction.getPaymentStatus())
                    .orderId(order.getId())
                    .build();

        } catch (Exception e) {
            throw new RestaurantException("Không thể tạo thanh toán: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void handlePaymentWebhook(Map<String, Object> webhookData) {
        try {
            WebhookData data = payOS.webhooks().verify(webhookData);
            log.info("Webhook verified: {}", data);

            Long transactionCode = Long.valueOf(data.getOrderCode());

            Transaction transaction = transactionRepository
                    .findByTransactionCode(transactionCode.toString())
                    .orElse(null);

            if (transaction == null) {
                log.warn("Transaction not found for orderCode: {}. This might be a test webhook.", transactionCode);
                return;
            }

            if ("PAID".equals(transaction.getPaymentStatus()) || "CANCELLED".equals(transaction.getPaymentStatus())) {
                log.info("Transaction {} already processed with status {}. Skipping.",
                        transactionCode, transaction.getPaymentStatus());
                return;
            }

            String paymentStatus = data.getCode(); // "00" = success
            log.info("Processing payment status: {} for transaction: {}", paymentStatus, transactionCode);

            if ("00".equals(paymentStatus) || "PAID".equals(paymentStatus)) {
                transaction.setPaymentStatus("PAID");
                Order order = transaction.getOrder();
                order.setStatus("PAID");
                orderRepository.save(order);

                log.info("✅ Payment successful for transaction: {}", transactionCode);

            } else if ("CANCELLED".equals(paymentStatus)) {
                transaction.setPaymentStatus("CANCELLED");
                log.info("Payment cancelled for transaction: {}", transactionCode);

            } else {
                transaction.setPaymentStatus("FAILED");
                log.warn("Payment failed for transaction: {}. Status: {}", transactionCode, paymentStatus);
            }

            transactionRepository.save(transaction);

        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public CashPaymentResponse processCashPayment(CashPaymentRequest request) {
        // 1. Lấy order
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 2. Số tiền gốc từ order
        BigDecimal originalAmount = order.getTotalAmount();

        // ✅ amountReceived LÀ SỐ TIỀN SAU KHI ĐÃ GIẢM GIÁ (từ frontend)
        BigDecimal finalAmount = request.getAmountReceived();

        // ✅ TÍNH DISCOUNT = ORIGINAL - FINAL
        BigDecimal discountAmount = originalAmount.subtract(finalAmount);

        Promotion promotion = null;
        Long promotionId = null;
        String promotionType = null;
        BigDecimal promotionValue = null;

        // 3. Xử lý promotion nếu có
        if (request.getPromotionCode() != null && !request.getPromotionCode().isEmpty()) {
            promotion = promotionRepository.findByCode(request.getPromotionCode())
                    .orElseThrow(() -> new RuntimeException("Promotion not found"));

            promotionId = promotion.getId();
            promotionType = promotion.getPromotionType();
            promotionValue = promotion.getValue();

            // Set promotion vào order
            order.setPromotion(promotion);
        }

        // 4. Tạo transaction
        Transaction transaction = Transaction.builder()
                .order(order)
                .amountPaid(finalAmount)
                .amountOriginal(originalAmount)
                .discountAmount(discountAmount)
                .promotionId(promotionId)
                .promotionType(promotionType)
                .promotionValue(promotionValue)
                .paymentMethod("CASH")
                .paymentStatus("PAID")
                .transactionTime(LocalDateTime.now())
                .transactionCode(generateTransactionCode())
                .build();

        transactionRepository.save(transaction);

        // 5. Cập nhật order status
        order.setStatus("PAID");
        order.setCompletedAt(LocalDateTime.now());
        orderRepository.save(order);

        // 6. Trả về response
        return CashPaymentResponse.builder()
                .transactionId(transaction.getId())
                .orderId(order.getId())
                .orderNumber(String.valueOf(order.getId()))
                .amountPaid(finalAmount)
                .amountOriginal(originalAmount)
                .discountAmount(discountAmount)
                .promotionCode(request.getPromotionCode())
                .paymentMethod("CASH")
                .paymentStatus("PAID")
                .transactionTime(transaction.getTransactionTime())
                .message("Thanh toán tiền mặt thành công")
                .build();
    }


    private String generateTransactionCode() {
        return "CASH-" + System.currentTimeMillis() + "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }


    @Data
    @AllArgsConstructor
    static class AggregatedItem {
        String name;
        Long price;
        Integer quantity;
    }
}