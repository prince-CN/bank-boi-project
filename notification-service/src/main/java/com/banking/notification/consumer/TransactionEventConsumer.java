package com.banking.notification.consumer;

import com.banking.notification.dto.FraudAlertEvent;
import com.banking.notification.dto.TransactionEvent;
import com.banking.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "${kafka.topics.transaction-success}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTransactionSuccess(TransactionEvent event) {
        log.info("📩 Received TRANSACTION_SUCCESS event: Transaction ID = {}",
                event.getTransactionId());

        try {
            notificationService.sendTransactionSuccessNotification(
                    event.getTransactionId(),
                    event.getFromAccount(),
                    event.getToAccount(),
                    event.getAmount());
            log.info("✅ Transaction success notification processed successfully");
        } catch (Exception e) {
            log.error("❌ Error processing transaction success notification: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${kafka.topics.fraud-alert}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeFraudAlert(FraudAlertEvent event) {
        log.info("🚨 Received FRAUD_ALERT event: Transaction ID = {}, Risk Score = {}",
                event.getTransactionId(), event.getRiskScore());

        try {
            notificationService.sendFraudAlert(
                    event.getTransactionId(),
                    event.getFromAccount(),
                    event.getToAccount(),
                    event.getAmount(),
                    event.getRiskScore(),
                    event.getReason());
            log.info("✅ Fraud alert notification processed successfully");
        } catch (Exception e) {
            log.error("❌ Error processing fraud alert notification: {}", e.getMessage(), e);
        }
    }
}
