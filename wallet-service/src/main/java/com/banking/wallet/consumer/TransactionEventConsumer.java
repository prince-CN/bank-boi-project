package com.banking.wallet.consumer;

import com.banking.wallet.dto.TransactionEvent;
import com.banking.wallet.dto.WalletUpdateEvent;
import com.banking.wallet.producer.WalletEventProducer;
import com.banking.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionEventConsumer {

    private final WalletService walletService;
    private final WalletEventProducer walletEventProducer;

    @KafkaListener(topics = "${kafka.topics.transaction-initiated}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTransactionInitiated(TransactionEvent event) {
        log.info("📩 Received TRANSACTION_INITIATED event: Transaction ID = {}",
                event.getTransactionId());

        try {
            // Process wallet updates
            WalletUpdateEvent walletUpdate = walletService.processTransaction(
                    event.getTransactionId(),
                    event.getFromAccount(),
                    event.getToAccount(),
                    event.getAmount());

            // Publish wallet update event
            walletEventProducer.sendWalletUpdateEvent(walletUpdate);

            log.info("✅ Wallet update processed successfully for transaction: {}",
                    event.getTransactionId());

        } catch (IllegalStateException e) {
            log.error("❌ Insufficient balance for transaction {}: {}",
                    event.getTransactionId(), e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error processing wallet update for transaction {}: {}",
                    event.getTransactionId(), e.getMessage(), e);
        }
    }
}
