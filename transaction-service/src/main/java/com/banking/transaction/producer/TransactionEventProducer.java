package com.banking.transaction.producer;

import com.banking.transaction.dto.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventProducer {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @Value("${kafka.topics.transaction-initiated}")
    private String transactionInitiated;

    @Value("${kafka.topics.transaction-success}")
    private String transactionSuccess;

    @Value("${kafka.topics.transaction-failed}")
    private String transactionFailed;

    public void sendTransactionInitiatedEvent(TransactionEvent event) {
        log.info("Sending TRANSACTION_INITIATED event for transaction ID: {}", event.getTransactionId());
        sendEvent(transactionInitiated, event);
    }

    public void sendTransactionSuccessEvent(TransactionEvent event) {
        log.info("Sending TRANSACTION_SUCCESS event for transaction ID: {}", event.getTransactionId());
        sendEvent(transactionSuccess, event);
    }

    public void sendTransactionFailedEvent(TransactionEvent event) {
        log.info("Sending TRANSACTION_FAILED event for transaction ID: {}", event.getTransactionId());
        sendEvent(transactionFailed, event);
    }

    private void sendEvent(String topic, TransactionEvent event) {
        CompletableFuture<SendResult<String, TransactionEvent>> future = kafkaTemplate.send(topic,
                String.valueOf(event.getTransactionId()), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Event sent successfully to topic {}: offset={}",
                        topic, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send event to topic {}: {}", topic, ex.getMessage());
            }
        });
    }
}
