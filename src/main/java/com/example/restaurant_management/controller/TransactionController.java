package com.example.restaurant_management.controller;

import com.example.restaurant_management.entity.Transaction;
import com.example.restaurant_management.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.createTransaction(transaction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, transaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Transaction> getTransactionByOrder(@PathVariable Long orderId) {
        return transactionService.getTransactionByOrder(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{transactionCode}")
    public ResponseEntity<Transaction> getTransactionByCode(@PathVariable String transactionCode) {
        return transactionService.getTransactionByCode(transactionCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/payment-method/{paymentMethod}")
    public ResponseEntity<List<Transaction>> getTransactionsByPaymentMethod(@PathVariable String paymentMethod) {
        return ResponseEntity.ok(transactionService.getTransactionsByPaymentMethod(paymentMethod));
    }

    @GetMapping("/cashier/{cashierId}")
    public ResponseEntity<List<Transaction>> getTransactionsByCashier(@PathVariable Long cashierId) {
        return ResponseEntity.ok(transactionService.getTransactionsByCashier(cashierId));
    }

    @GetMapping("/between")
    public ResponseEntity<List<Transaction>> getTransactionsBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(transactionService.getTransactionsBetween(start, end));
    }
}
