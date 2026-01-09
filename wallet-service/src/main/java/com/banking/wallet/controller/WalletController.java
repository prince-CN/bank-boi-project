package com.banking.wallet.controller;

import com.banking.wallet.entity.Wallet;
import com.banking.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/create")
    public ResponseEntity<Wallet> createWallet(
            @RequestParam String accountNumber,
            @RequestParam(required = false) BigDecimal initialBalance) {
        try {
            Wallet wallet = walletService.createWallet(accountNumber, initialBalance);
            return ResponseEntity.status(HttpStatus.CREATED).body(wallet);
        } catch (IllegalArgumentException e) {
            log.error("Error creating wallet: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<Wallet> getWallet(@PathVariable String accountNumber) {
        try {
            Wallet wallet = walletService.getWalletByAccountNumber(accountNumber);
            return ResponseEntity.ok(wallet);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable String accountNumber) {
        try {
            Wallet wallet = walletService.getWalletByAccountNumber(accountNumber);
            return ResponseEntity.ok(wallet.getBalance());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
