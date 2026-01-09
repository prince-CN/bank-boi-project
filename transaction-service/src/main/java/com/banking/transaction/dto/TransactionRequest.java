package com.banking.transaction.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    
    @NotBlank(message = "From account is required")
    @Size(min = 3, max = 50, message = "From account must be between 3 and 50 characters")
    private String fromAccount;
    
    @NotBlank(message = "To account is required")
    @Size(min = 3, max = 50, message = "To account must be between 3 and 50 characters")
    private String toAccount;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @DecimalMax(value = "1000000.00", message = "Amount must not exceed 1,000,000")
    @Digits(integer = 15, fraction = 2, message = "Invalid amount format")
    private BigDecimal amount;
    
    private String description;
}
