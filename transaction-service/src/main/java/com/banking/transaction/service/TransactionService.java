package com.banking.transaction.service;

import com.banking.transaction.dto.TransactionEvent;
import com.banking.transaction.dto.TransactionRequest;
import com.banking.transaction.dto.TransactionResponse;
import com.banking.transaction.entity.Transaction;
import com.banking.transaction.entity.TransactionStatus;
import com.banking.transaction.producer.TransactionEventProducer;
import com.banking.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionEventProducer eventProducer;

    @Transactional
    public TransactionResponse initiateTransaction(TransactionRequest request) {
        log.info("Initiating transaction from {} to {} for amount {}",
                request.getFromAccount(), request.getToAccount(), request.getAmount());

        // Validate accounts
        if (request.getFromAccount().equalsIgnoreCase(request.getToAccount())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        // Create transaction entity
        Transaction transaction = new Transaction();
        transaction.setFromAccount(request.getFromAccount());
        transaction.setToAccount(request.getToAccount());
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setStatus(TransactionStatus.PENDING);

        // Save to database
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction saved with ID: {}", savedTransaction.getId());

        // Publish TRANSACTION_INITIATED event to Kafka
        TransactionEvent event = mapToEvent(savedTransaction);
        eventProducer.sendTransactionInitiatedEvent(event);

        return mapToResponse(savedTransaction);
    }

    @Transactional
    public TransactionResponse updateTransactionStatus(Long transactionId, TransactionStatus status) {
        log.info("Updating transaction {} status to {}", transactionId, status);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionId));

        transaction.setStatus(status);
        Transaction updatedTransaction = transactionRepository.save(transaction);

        // Publish appropriate event based on status
        TransactionEvent event = mapToEvent(updatedTransaction);
        if (status == TransactionStatus.SUCCESS) {
            eventProducer.sendTransactionSuccessEvent(event);
        } else if (status == TransactionStatus.FAILED) {
            eventProducer.sendTransactionFailedEvent(event);
        }

        return mapToResponse(updatedTransaction);
    }

    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + id));
        return mapToResponse(transaction);
    }

    public List<TransactionResponse> getTransactionHistory(String accountNumber) {
        List<Transaction> transactions = transactionRepository
                .findByFromAccountOrToAccountOrderByCreatedAtDesc(accountNumber, accountNumber);
        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getPendingTransactions() {
        List<Transaction> transactions = transactionRepository.findByStatus(TransactionStatus.PENDING);
        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setFromAccount(transaction.getFromAccount());
        response.setToAccount(transaction.getToAccount());
        response.setAmount(transaction.getAmount());
        response.setStatus(transaction.getStatus());
        response.setDescription(transaction.getDescription());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setUpdatedAt(transaction.getUpdatedAt());
        return response;
    }

    private TransactionEvent mapToEvent(Transaction transaction) {
        TransactionEvent event = new TransactionEvent();
        event.setTransactionId(transaction.getId());
        event.setFromAccount(transaction.getFromAccount());
        event.setToAccount(transaction.getToAccount());
        event.setAmount(transaction.getAmount());
        event.setStatus(transaction.getStatus().name());
        event.setDescription(transaction.getDescription());
        event.setTimestamp(transaction.getCreatedAt());
        return event;
    }
}
