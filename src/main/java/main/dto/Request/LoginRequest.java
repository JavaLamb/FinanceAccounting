package main.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Введите корректный email")
    private String username;

    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
}
