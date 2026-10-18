package main.dto.Request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ChangeTransactionRequest(
        @Positive(message = "Amount should be positive")
        @DecimalMax(value = "1000000.00", message = "Maximum amount of single transaction is 1_000_000")
        @Digits(integer = 9, fraction = 2, message = "Incorrect format of amount")
        @NotNull(message = "Amount cannot be empty")
        BigDecimal amount
) {
}
