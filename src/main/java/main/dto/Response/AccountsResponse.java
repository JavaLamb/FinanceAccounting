package main.dto.Response;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import main.entities.AccountType;

import java.math.BigDecimal;

@Builder
@Jacksonized
public record AccountsResponse(
        Long id,
        String name,
        BigDecimal balance,
        AccountType accountType) {
}
