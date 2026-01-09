package com.banking.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletUpdateEvent {

    private String accountNumber;
    private BigDecimal previousBalance;
    private BigDecimal newBalance;
    private BigDecimal amount;
    private String operation; // DEBIT or CREDIT
    private Long transactionId;
    private LocalDateTime timestamp = LocalDateTime.now();
}
