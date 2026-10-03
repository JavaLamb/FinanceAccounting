package main.dto.Request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateTransferRequest(
        @NotNull(message = "Destination account cannot be empty")
        Long toAccountId,
        @NotNull(message = "CategoryId cannot be empty")
        Long categoryId,
        @NotNull(message = "Amount cannot be empty")
        @Positive
        @DecimalMax(value = "1000000.00", message = "Maximum amount of single transfer is 1_000_000")
        @Digits(integer = 9, fraction = 2, message = "Incorrect format of amount")
        BigDecimal amount) {
}
