package main.dto.Response;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record CategoryResponse(
        String categoryName,
        long id) {
}
