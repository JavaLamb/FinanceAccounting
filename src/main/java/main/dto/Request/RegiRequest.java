package main.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegiRequest(
        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Incorrect format of email")
        String username,

        @NotBlank(message = "Password cannot be empty")
        @Size(min = 8, max = 32, message = "Length must be from {min} to {max} symbols")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
                message = "The password must contain at least one digit, one uppercase letter, one lowercase letter, and one special character."
        )
        String password
) {
}
