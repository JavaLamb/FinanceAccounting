package main.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import main.entities.AccountType;

@Data
public class CreateAccountRequest {
    @NotBlank(message = "Имя аккаунта не может быть пустым")
    @Size(min = 1, max = 255, message = "Длина должна быть от {min} до {max} символов")
    private String name;
    @NotBlank(message = "Тип аккаунта не может быть пустым")
    private AccountType accountType;
}
