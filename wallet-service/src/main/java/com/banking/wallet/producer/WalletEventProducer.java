package com.banking.wallet.producer;

import com.banking.wallet.dto.WalletUpdateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WalletEventProducer {

    private final KafkaTemplate<String, WalletUpdateEvent> kafkaTemplate;

    @Value("${kafka.topics.wallet-updated}")
    private String walletUpdatedTopic;

    public void sendWalletUpdateEvent(WalletUpdateEvent event) {
        log.info("Sending WALLET_UPDATED event for account: {}", event.getAccountNumber());

        kafkaTemplate.send(walletUpdatedTopic, event.getAccountNumber(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Wallet update event sent successfully: offset={}",
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send wallet update event: {}", ex.getMessage());
                    }
                });
    }
}
