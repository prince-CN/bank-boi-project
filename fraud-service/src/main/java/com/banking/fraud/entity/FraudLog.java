package com.banking.fraud.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "fraud_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudLog {

    @Id
    private String id;

    private Long transactionId;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private Integer riskScore; // 0-100
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL
    private String reason;
    private boolean flagged;
    private LocalDateTime timestamp = LocalDateTime.now();
}
