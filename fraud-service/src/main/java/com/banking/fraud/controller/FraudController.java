package com.banking.fraud.controller;

import com.banking.fraud.entity.FraudLog;
import com.banking.fraud.service.FraudDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/fraud")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FraudController {

    private final FraudDetectionService fraudDetectionService;

    @GetMapping("/flagged")
    public ResponseEntity<List<FraudLog>> getFlaggedTransactions() {
        log.info("Fetching flagged transactions");
        List<FraudLog> flagged = fraudDetectionService.getFlaggedTransactions();
        return ResponseEntity.ok(flagged);
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<FraudLog>> getFraudLogsByAccount(@PathVariable String accountNumber) {
        log.info("Fetching fraud logs for account: {}", accountNumber);
        List<FraudLog> logs = fraudDetectionService.getFraudLogsByAccount(accountNumber);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/all")
    public ResponseEntity<List<FraudLog>> getAllFraudLogs() {
        log.info("Fetching all fraud logs");
        List<FraudLog> logs = fraudDetectionService.getAllFraudLogs();
        return ResponseEntity.ok(logs);
    }
}
