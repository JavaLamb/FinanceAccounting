package main.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import main.entities.AccountType;


public record CreateAccountRequest(
        @NotBlank(message = "Имя аккаунта не может быть пустым")
        @Size(min = 1, max = 255, message = "Длина должна быть от {min} до {max} символов")
        String name,

        @NotNull(message = "Тип аккаунта не может быть пустым")
        AccountType accountType) {
}
