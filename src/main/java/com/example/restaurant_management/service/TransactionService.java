package com.example.restaurant_management.service;

import com.example.restaurant_management.entity.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
    List<Transaction> getAllTransactions();
    Optional<Transaction> getTransactionById(Long id);
    Transaction createTransaction(Transaction transaction);
    Transaction updateTransaction(Long id, Transaction transaction);
    void deleteTransaction(Long id);
    Optional<Transaction> getTransactionByCode(String transactionCode);
    List<Transaction> getTransactionsByPaymentMethod(String paymentMethod);
    List<Transaction> getTransactionsByCashier(Long cashierId);
    List<Transaction> getTransactionsBetween(LocalDateTime start, LocalDateTime end);
}
