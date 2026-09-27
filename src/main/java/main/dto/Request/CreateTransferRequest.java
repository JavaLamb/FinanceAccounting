package main.dto.Request;

import lombok.Data;
import main.entities.Account;

import java.math.BigDecimal;

@Data
public class CreateTransferRequest {
    Long toAccountId;
    Long categoryId;
    BigDecimal amount;
}
