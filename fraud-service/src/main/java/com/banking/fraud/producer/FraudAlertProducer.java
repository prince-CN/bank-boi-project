package com.banking.fraud.producer;

import com.banking.fraud.dto.FraudAlertEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FraudAlertProducer {

    private final KafkaTemplate<String, FraudAlertEvent> kafkaTemplate;

    @Value("${kafka.topics.fraud-alert}")
    private String fraudAlertTopic;

    public void sendFraudAlert(FraudAlertEvent event) {
        log.warn("🚨 Sending FRAUD_ALERT for transaction: {}", event.getTransactionId());

        kafkaTemplate.send(fraudAlertTopic, String.valueOf(event.getTransactionId()), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.warn("Fraud alert sent successfully: offset={}",
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send fraud alert: {}", ex.getMessage());
                    }
                });
    }
}
