package main.dto.Request;

import lombok.Data;
import main.entities.AccountType;

@Data
public class CreateAccountRequest {
    private String name;
    private AccountType accountType;
}
