package main.dto.Request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import main.entities.TransactionType;

import java.math.BigDecimal;

@Data
public class CreateRegularTransactionRequest {
    @NotNull(message = "Необходимо указать тип транзакции")
    TransactionType transactionType;
    @NotNull(message = "Необходимо указать категорию")
    Long categoryId;
    @NotNull(message = "Сумма обязательна для заполнения")
    @Positive
    @DecimalMax(value = "1000000.00", message = "Максимальный разовый перевод 1_000_000")
    @Digits(integer = 9, fraction = 2, message = "Некорректный формат суммы")
    BigDecimal amount;
}
