package main.dto.Response;

import lombok.Builder;
import main.entities.AccountType;

import java.math.BigDecimal;

@Builder
public record AccountsResponse(
        Long id,
        String name,
        BigDecimal balance,
        AccountType accountType) {
}
