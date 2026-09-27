package main.dto.Request;

import lombok.Data;
import main.entities.TransactionType;

import java.math.BigDecimal;

@Data
public class CreateRegularTransactionRequest {
    TransactionType transactionType;
    Long transactionCategoryId;
    BigDecimal amount;
}
