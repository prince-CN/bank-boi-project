package com.banking.notification.service;

import com.banking.notification.entity.Notification;
import com.banking.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void sendTransactionSuccessNotification(Long transactionId, String fromAccount,
            String toAccount, BigDecimal amount) {
        log.info("Sending transaction success notification for transaction ID: {}", transactionId);

        // Create notification for sender
        Notification senderNotification = new Notification();
        senderNotification.setTransactionId(transactionId);
        senderNotification.setType("SUCCESS");
        senderNotification.setRecipient(fromAccount);
        senderNotification.setFromAccount(fromAccount);
        senderNotification.setToAccount(toAccount);
        senderNotification.setAmount(amount);
        senderNotification.setMessage(
                String.format("Payment of ₹%.2f sent successfully to %s", amount, toAccount));
        senderNotification.setSent(true);
        notificationRepository.save(senderNotification);

        // Create notification for receiver
        Notification receiverNotification = new Notification();
        receiverNotification.setTransactionId(transactionId);
        receiverNotification.setType("SUCCESS");
        receiverNotification.setRecipient(toAccount);
        receiverNotification.setFromAccount(fromAccount);
        receiverNotification.setToAccount(toAccount);
        receiverNotification.setAmount(amount);
        receiverNotification.setMessage(
                String.format("You received ₹%.2f from %s", amount, fromAccount));
        receiverNotification.setSent(true);
        notificationRepository.save(receiverNotification);

        // Simulate sending email/SMS
        log.info("✉️ Email sent to {}: Payment of ₹{} sent to {}", fromAccount, amount, toAccount);
        log.info("✉️ Email sent to {}: Received ₹{} from {}", toAccount, amount, fromAccount);
    }

    public void sendFraudAlert(Long transactionId, String fromAccount, String toAccount,
            BigDecimal amount, Integer riskScore, String reason) {
        log.warn("⚠️ FRAUD ALERT for transaction ID: {} - Risk Score: {}", transactionId, riskScore);

        Notification fraudAlert = new Notification();
        fraudAlert.setTransactionId(transactionId);
        fraudAlert.setType("FRAUD_ALERT");
        fraudAlert.setRecipient(fromAccount);
        fraudAlert.setFromAccount(fromAccount);
        fraudAlert.setToAccount(toAccount);
        fraudAlert.setAmount(amount);
        fraudAlert.setMessage(
                String.format("⚠️ FRAUD ALERT: Suspicious transaction of ₹%.2f detected. " +
                        "Risk Score: %d. Reason: %s", amount, riskScore, reason));
        fraudAlert.setSent(true);
        notificationRepository.save(fraudAlert);

        // Simulate sending urgent alert
        log.warn("🚨 URGENT SMS sent to {}: Suspicious transaction detected!", fromAccount);
    }

    public List<Notification> getNotificationsByRecipient(String recipient) {
        return notificationRepository.findByRecipient(recipient);
    }

    public List<Notification> getNotificationsByTransaction(Long transactionId) {
        return notificationRepository.findByTransactionId(transactionId);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
}
