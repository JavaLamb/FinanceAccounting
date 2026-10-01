package main.dto.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import main.entities.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;


@Builder
@Jacksonized
public record TransactionResponse(
        Long id,
        TransactionType transactionType,
        Long fromAccountId,
        Long toAccountId,
        String categoryName,
        BigDecimal amount,
        //Просто для красоты вывода
        @JsonFormat(pattern = "dd.MM.yyyy HH:mm", timezone = "UTC")
        Instant dateTime) {
}