package main.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegiRequest(
        @NotBlank(message = "Email не может быть пустым")
        @Email(message = "Введите корректный email")
        String username,

        @NotBlank(message = "Пароль не может быть пустым")
        @Size(min = 8, max = 32, message = "Длина пароля должна быть от {min} до {max} символов")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
                message = "Пароль должен содержать минимум одну цифру, заглавную и строчную букву, а также спецсимвол"
        )
        String password) {
}
