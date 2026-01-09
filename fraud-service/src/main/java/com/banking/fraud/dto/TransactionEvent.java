package com.banking.fraud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {

    private Long transactionId;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private String status;
    private String description;
    private LocalDateTime timestamp;
}
