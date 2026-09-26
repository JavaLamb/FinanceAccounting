package main.dto.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import main.entities.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Accessors(chain = true)
@Setter
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class TransactionResponse {
    Long id;
    TransactionType transactionType;
    Long fromAccountId;
    Long toAccountId;
    String categoryName;
    BigDecimal amount;
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    LocalDateTime dateTime;
}
