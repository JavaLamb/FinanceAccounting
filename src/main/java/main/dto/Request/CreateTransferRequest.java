package main.dto.Request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateTransferRequest(
        @NotNull(message = "Необходимо указать id аккаунта на который произойдет перевод")
        Long toAccountId,
        @NotNull(message = "Необходимо указать id категории")
        Long categoryId,
        @NotNull(message = "Необходимо размер транзакции")
        @Positive
        @DecimalMax(value = "1000000.00", message = "Максимальный разовый перевод 1_000_000")
        @Digits(integer = 9, fraction = 2, message = "Некорректный формат суммы")
        BigDecimal amount) {
}
