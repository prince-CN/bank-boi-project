package com.banking.notification.repository;

import com.banking.notification.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByTransactionId(Long transactionId);

    List<Notification> findByRecipient(String recipient);

    List<Notification> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<Notification> findByType(String type);

    List<Notification> findBySent(boolean sent);
}
