package main.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record CategoryRequest(
        @NotBlank(message = "Название категории не может быть пустым")
        @Size(min = 1, max = 255, message = "Длина должна быть от {min} до {max} символов")
        String categoryName) {
}
