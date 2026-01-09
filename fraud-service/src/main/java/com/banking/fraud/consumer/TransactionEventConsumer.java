package com.banking.fraud.consumer;

import com.banking.fraud.dto.FraudAlertEvent;
import com.banking.fraud.dto.TransactionEvent;
import com.banking.fraud.entity.FraudLog;
import com.banking.fraud.producer.FraudAlertProducer;
import com.banking.fraud.service.FraudDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventConsumer {

    private final FraudDetectionService fraudDetectionService;
    private final FraudAlertProducer fraudAlertProducer;

    @KafkaListener(topics = "${kafka.topics.transaction-initiated}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTransactionInitiated(TransactionEvent event) {
        log.info("📩 Received TRANSACTION_INITIATED event: Transaction ID = {}",
                event.getTransactionId());

        try {
            // Analyze transaction for fraud
            FraudLog fraudLog = fraudDetectionService.analyzeTransaction(
                    event.getTransactionId(),
                    event.getFromAccount(),
                    event.getToAccount(),
                    event.getAmount());

            // If fraud detected, send alert
            if (fraudLog.isFlagged()) {
                FraudAlertEvent alertEvent = new FraudAlertEvent();
                alertEvent.setTransactionId(fraudLog.getTransactionId());
                alertEvent.setFromAccount(fraudLog.getFromAccount());
                alertEvent.setToAccount(fraudLog.getToAccount());
                alertEvent.setAmount(fraudLog.getAmount());
                alertEvent.setRiskScore(fraudLog.getRiskScore());
                alertEvent.setReason(fraudLog.getReason());

                fraudAlertProducer.sendFraudAlert(alertEvent);
                log.warn("🚨 Fraud alert published for transaction: {}", event.getTransactionId());
            } else {
                log.info("✅ Transaction {} passed fraud checks", event.getTransactionId());
            }

        } catch (Exception e) {
            log.error("❌ Error analyzing transaction {}: {}",
                    event.getTransactionId(), e.getMessage(), e);
        }
    }
}
