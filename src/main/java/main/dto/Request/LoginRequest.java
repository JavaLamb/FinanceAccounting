package main.dto.Request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email cannot be empty")
        String username,

        @NotBlank(message = "Password cannot be empty")
        String password
) {
}
