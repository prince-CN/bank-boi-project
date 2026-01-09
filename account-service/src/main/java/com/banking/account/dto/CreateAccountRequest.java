package com.banking.account.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Account type is required")
    @Pattern(regexp = "SAVINGS|CURRENT|WALLET", message = "Invalid account type")
    private String accountType;

    @DecimalMin(value = "0.0", message = "Initial balance must be >= 0")
    private BigDecimal initialBalance = BigDecimal.ZERO;
}
