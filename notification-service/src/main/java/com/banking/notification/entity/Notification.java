package com.banking.notification.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    private String id;

    private Long transactionId;
    private String type; // SUCCESS, FRAUD_ALERT, FAILURE
    private String message;
    private String recipient;
    private BigDecimal amount;
    private String fromAccount;
    private String toAccount;
    private LocalDateTime timestamp = LocalDateTime.now();
    private boolean sent = false;
}
