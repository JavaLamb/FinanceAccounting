package main.dto.Request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateTransferRequest {
    @NotNull
    Long toAccountId;
    @NotNull
    Long categoryId;
    @NotNull
    @Positive
    @DecimalMax(value = "1000000.00", message = "Максимальный разовый перевод 1_000_000")
    @Digits(integer = 9, fraction = 2, message = "Некорректный формат суммы")
    BigDecimal amount;
}
