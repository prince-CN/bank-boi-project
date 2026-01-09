package com.banking.fraud.service;

import com.banking.fraud.entity.FraudLog;
import com.banking.fraud.repository.FraudLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionService {

    private final FraudLogRepository fraudLogRepository;

    @Value("${fraud.detection.high-amount-threshold}")
    private BigDecimal highAmountThreshold;

    @Value("${fraud.detection.rapid-transaction-window-minutes}")
    private int rapidTransactionWindowMinutes;

    @Value("${fraud.detection.rapid-transaction-count}")
    private int rapidTransactionCount;

    public FraudLog analyzeTransaction(Long transactionId, String fromAccount,
            String toAccount, BigDecimal amount) {
        log.info("🔍 Analyzing transaction {} for fraud detection", transactionId);

        FraudLog fraudLog = new FraudLog();
        fraudLog.setTransactionId(transactionId);
        fraudLog.setFromAccount(fromAccount);
        fraudLog.setToAccount(toAccount);
        fraudLog.setAmount(amount);

        List<String> reasons = new ArrayList<>();
        int riskScore = 0;

        // Rule 1: High Amount Detection
        if (amount.compareTo(highAmountThreshold) > 0) {
            reasons.add("High amount transaction: ₹" + amount);
            riskScore += 50;
            log.warn("⚠️ High amount detected: ₹{}", amount);
        }

        // Rule 2: Rapid Transaction Detection
        LocalDateTime windowStart = LocalDateTime.now().minusMinutes(rapidTransactionWindowMinutes);
        long recentTransactionCount = fraudLogRepository
                .countByFromAccountAndTimestampAfter(fromAccount, windowStart);

        if (recentTransactionCount >= rapidTransactionCount) {
            reasons.add("Rapid transactions: " + recentTransactionCount +
                    " in last " + rapidTransactionWindowMinutes + " minutes");
            riskScore += 40;
            log.warn("⚠️ Rapid transactions detected: {} transactions", recentTransactionCount);
        }

        // Rule 3: Round Amount (potential money laundering)
        if (amount.remainder(BigDecimal.valueOf(10000)).equals(BigDecimal.ZERO) &&
                amount.compareTo(BigDecimal.valueOf(10000)) >= 0) {
            reasons.add("Round amount: ₹" + amount);
            riskScore += 10;
            log.warn("⚠️ Round amount detected: ₹{}", amount);
        }

        // Calculate risk level
        String riskLevel;
        if (riskScore >= 75) {
            riskLevel = "CRITICAL";
            fraudLog.setFlagged(true);
        } else if (riskScore >= 50) {
            riskLevel = "HIGH";
            fraudLog.setFlagged(true);
        } else if (riskScore >= 25) {
            riskLevel = "MEDIUM";
            fraudLog.setFlagged(false);
        } else {
            riskLevel = "LOW";
            fraudLog.setFlagged(false);
        }

        fraudLog.setRiskScore(riskScore);
        fraudLog.setRiskLevel(riskLevel);
        fraudLog.setReason(String.join("; ", reasons.isEmpty() ? List.of("Normal transaction") : reasons));

        // Save fraud log
        FraudLog saved = fraudLogRepository.save(fraudLog);

        if (fraudLog.isFlagged()) {
            log.error("🚨 FRAUD DETECTED! Transaction: {}, Risk Score: {}, Level: {}",
                    transactionId, riskScore, riskLevel);
        } else {
            log.info("✅ Transaction {} analyzed: Risk Score: {}, Level: {}",
                    transactionId, riskScore, riskLevel);
        }

        return saved;
    }

    public List<FraudLog> getFlaggedTransactions() {
        return fraudLogRepository.findByFlagged(true);
    }

    public List<FraudLog> getFraudLogsByAccount(String accountNumber) {
        return fraudLogRepository.findByFromAccount(accountNumber);
    }

    public List<FraudLog> getAllFraudLogs() {
        return fraudLogRepository.findAll();
    }
}
