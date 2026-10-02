package main.dto.Response;

import lombok.Builder;

@Builder
public record UserResponse(
        Long id,
        String email) {
}