package main.dto.Response;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Builder
@Jacksonized
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, List<String>> validationErrors) {
}
