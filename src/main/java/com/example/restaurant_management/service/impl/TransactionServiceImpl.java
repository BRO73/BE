package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.entity.Transaction;
import com.example.restaurant_management.repository.TransactionRepository;
import com.example.restaurant_management.service.TransactionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction updateTransaction(Long id, Transaction transaction) {
        transaction.setId(id);
        return transactionRepository.save(transaction);
    }

    @Override
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }


    @Override
    public Optional<Transaction> getTransactionByCode(String transactionCode) {
        return transactionRepository.findByTransactionCode(transactionCode);
    }

    @Override
    public List<Transaction> getTransactionsByPaymentMethod(String paymentMethod) {
        return transactionRepository.findByPaymentMethod(paymentMethod);
    }

    @Override
    public List<Transaction> getTransactionsByCashier(Long cashierId) {
        return transactionRepository.findByCashierUserId(cashierId);
    }

    @Override
    public List<Transaction> getTransactionsBetween(LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByTransactionTimeBetween(start, end);
    }
}
