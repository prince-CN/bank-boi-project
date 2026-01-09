package com.banking.notification.controller;

import com.banking.notification.entity.Notification;
import com.banking.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<List<Notification>> getNotificationsByRecipient(
            @PathVariable String recipient) {
        log.info("Fetching notifications for recipient: {}", recipient);
        List<Notification> notifications = notificationService.getNotificationsByRecipient(recipient);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<List<Notification>> getNotificationsByTransaction(
            @PathVariable Long transactionId) {
        log.info("Fetching notifications for transaction ID: {}", transactionId);
        List<Notification> notifications = notificationService.getNotificationsByTransaction(transactionId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        log.info("Fetching all notifications");
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }
}
