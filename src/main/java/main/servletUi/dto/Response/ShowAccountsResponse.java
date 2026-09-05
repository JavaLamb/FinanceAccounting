package main.servletUi.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import main.entities.AccountType;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ShowAccountsResponse {
    Integer id;
    String name;
    BigDecimal balance;
    AccountType accountType;
}
