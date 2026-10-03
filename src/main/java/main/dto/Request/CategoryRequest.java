package main.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record CategoryRequest(
        @NotBlank(message = "Category name cannot be empty")
        @Size(min = 1, max = 255, message = "Length must be from {min} to {max} symbols")
        String categoryName
) {
}
