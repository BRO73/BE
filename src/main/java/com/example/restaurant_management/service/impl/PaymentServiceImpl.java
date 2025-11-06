package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.exception.RestaurantException;
import com.example.restaurant_management.dto.request.PaymentRequestDTO;
import com.example.restaurant_management.dto.response.PaymentResponseDTO;
import com.example.restaurant_management.entity.Order;
import com.example.restaurant_management.entity.OrderDetail;
import com.example.restaurant_management.entity.Transaction;
import com.example.restaurant_management.repository.OrderRepository;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PayOS payOS;
    private final OrderRepository orderRepository;
    private final TransactionRepository transactionRepository;

    @Data
    @AllArgsConstructor
    static class ItemInfo {
        Long price;
        Integer quantity;
    }

    @Override
    @Transactional
    public PaymentResponseDTO createPaymentLink(PaymentRequestDTO request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RestaurantException("Order not found"));

        Map<String, ItemInfo> menuItemMap = new HashMap<>();

        for (OrderDetail detail : order.getOrderDetails()) {
            if (detail.getStatus().equals("PENDING") || detail.getStatus().equals("CONFIRMED")) {
                String menuItemName = detail.getMenuItem().getName();
                Long itemPrice = detail.getPriceAtOrder().longValue() / 100;

                menuItemMap.merge(
                        menuItemName,
                        new ItemInfo(itemPrice, detail.getQuantity()),
                        (existing, newItem) -> new ItemInfo(
                                existing.price,
                                existing.quantity + newItem.quantity
                        )
                );
            }
        }

        List<PaymentLinkItem> items = new ArrayList<>();
        menuItemMap.forEach((menuItemName, info) -> {
            PaymentLinkItem item = PaymentLinkItem.builder()
                    .name(menuItemName)
                    .quantity(info.quantity)
                    .price(info.price)
                    .build();
            items.add(item);
        });

        Long totalAmount = order.getTotalAmount().longValue();
        Long amount = Math.max(totalAmount / 100, 2000L);
        Long orderCode = System.currentTimeMillis() / 1000;

        CreatePaymentLinkRequest paymentRequest = CreatePaymentLinkRequest.builder()
                .orderCode(orderCode)
                .description(String.format("Thanh toán đơn hàng #%d", order.getId()))
                .amount(amount)
                .items(items)
                .returnUrl(request.getReturnUrl())
                .cancelUrl(request.getCancelUrl())
                .build();

        try {
            CreatePaymentLinkResponse payosResponse = payOS.paymentRequests().create(paymentRequest);

            Transaction transaction = Transaction.builder()
                    .order(order)
                    .amountPaid(order.getTotalAmount())
                    .paymentMethod("PAYOS")
                    .transactionTime(LocalDateTime.now())
                    .transactionCode(orderCode.toString())
                    .paymentStatus("PENDING")
                    .paymentLinkId(payosResponse.getPaymentLinkId())
                    .checkoutUrl(payosResponse.getCheckoutUrl())
                    .build();

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
            // Verify webhook từ PayOS
            WebhookData data = payOS.webhooks().verify(webhookData);
            log.info("Webhook verified: {}", data);

            Long transactionCode = Long.valueOf(data.getOrderCode());

            // ✅ TÌM TRANSACTION - KHÔNG THROW EXCEPTION
            Transaction transaction = transactionRepository
                    .findByTransactionCode(transactionCode.toString())
                    .orElse(null);

            if (transaction == null) {
                log.warn("Transaction not found for orderCode: {}. This might be a test webhook.", transactionCode);
                // ✅ RETURN BÌNH THƯỜNG - PayOS sẽ nhận 200 OK
                return;
            }

            // Lấy payment status từ webhook
            String paymentStatus = data.getCode(); // "00" = success
            log.info("Processing payment status: {} for transaction: {}", paymentStatus, transactionCode);

            // Cập nhật transaction status
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
            // ✅ QUAN TRỌNG: Log error NHƯNG KHÔNG THROW
            log.error("Error processing webhook: {}", e.getMessage(), e);
            // Webhook vẫn trả 200 OK cho PayOS
        }
    }
}