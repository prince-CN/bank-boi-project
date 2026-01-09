package com.banking.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudAlertEvent {

    private Long transactionId;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private Integer riskScore;
    private String reason;
    private LocalDateTime timestamp;
}
