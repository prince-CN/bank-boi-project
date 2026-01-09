package com.banking.wallet.service;

import com.banking.wallet.dto.WalletUpdateEvent;
import com.banking.wallet.entity.Wallet;
import com.banking.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {

    private final WalletRepository walletRepository;

    @Transactional
    public Wallet createWallet(String accountNumber, BigDecimal initialBalance) {
        log.info("Creating wallet for account: {}", accountNumber);

        if (walletRepository.existsByAccountNumber(accountNumber)) {
            throw new IllegalArgumentException("Wallet already exists for account: " + accountNumber);
        }

        Wallet wallet = new Wallet();
        wallet.setAccountNumber(accountNumber);
        wallet.setBalance(initialBalance != null ? initialBalance : BigDecimal.ZERO);

        return walletRepository.save(wallet);
    }

    @Transactional
    public WalletUpdateEvent processTransaction(Long transactionId, String fromAccount,
            String toAccount, BigDecimal amount) {
        log.info("Processing transaction {} - Debit from {}, Credit to {}, Amount: {}",
                transactionId, fromAccount, toAccount, amount);

        // Get wallets (create if don't exist)
        Wallet fromWallet = getOrCreateWallet(fromAccount);
        Wallet toWallet = getOrCreateWallet(toAccount);

        // Check sufficient balance
        if (fromWallet.getBalance().compareTo(amount) < 0) {
            log.error("Insufficient balance in account: {}. Available: {}, Required: {}",
                    fromAccount, fromWallet.getBalance(), amount);
            throw new IllegalStateException("Insufficient balance");
        }

        // Debit from sender
        BigDecimal oldFromBalance = fromWallet.getBalance();
        fromWallet.setBalance(fromWallet.getBalance().subtract(amount));
        walletRepository.save(fromWallet);
        log.info("Debited {} from {}. New balance: {}", amount, fromAccount, fromWallet.getBalance());

        // Credit to receiver

        toWallet.setBalance(toWallet.getBalance().add(amount));
        walletRepository.save(toWallet);
        log.info("Credited {} to {}. New balance: {}", amount, toAccount, toWallet.getBalance());

        // Create wallet update event
        WalletUpdateEvent event = new WalletUpdateEvent();
        event.setAccountNumber(fromAccount);
        event.setPreviousBalance(oldFromBalance);
        event.setNewBalance(fromWallet.getBalance());
        event.setAmount(amount);
        event.setOperation("TRANSFER");
        event.setTransactionId(transactionId);

        return event;
    }

    public Wallet getWalletByAccountNumber(String accountNumber) {
        return walletRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + accountNumber));
    }

    private Wallet getOrCreateWallet(String accountNumber) {
        return walletRepository.findByAccountNumber(accountNumber)
                .orElseGet(() -> {
                    log.info("Wallet not found for {}. Creating with initial balance 10000", accountNumber);
                    return createWallet(accountNumber, new BigDecimal("10000.00"));
                });
    }
}
