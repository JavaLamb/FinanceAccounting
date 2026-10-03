package main.dto.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import main.entities.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;


@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
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