package main.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Введите корректный email")
        String username,

        @NotBlank(message = "Пароль не может быть пустым")
        String password) {
}
