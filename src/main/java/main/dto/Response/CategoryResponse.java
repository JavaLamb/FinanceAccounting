package main.dto.Response;

import lombok.Builder;

@Builder
public record CategoryResponse(
        String categoryName,
        long id) {
}
