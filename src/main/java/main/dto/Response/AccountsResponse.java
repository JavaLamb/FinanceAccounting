package main.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import main.entities.AccountType;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class AccountsResponse {
    Long id;
    String name;
    BigDecimal balance;
    AccountType accountType;
}
