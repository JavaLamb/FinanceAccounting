package main.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import main.entities.AccountType;


public record CreateAccountRequest(
        @NotBlank(message = "Account name cannot be empty")
        @Size(min = 1, max = 255, message = "Length must be from {min} to {max} symbols")
        String name,

        @NotNull(message = "Account type cannot be empty")
        AccountType accountType
) {
}
