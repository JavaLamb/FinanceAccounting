package main.converters;

import main.dto.Response.TransactionResponse;
import main.entities.Transaction;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TransactionToTransactionResponseConverter implements Converter<Transaction, TransactionResponse> {
    @Override
    public TransactionResponse convert(Transaction source) {
        return TransactionResponse.builder()
                .id(source.getId())
                .transactionType(source.getTransactionType())
                .categoryName(source.getCategory().getTransactionCategoryName())
                .amount(source.getAmount())
                .dateTime(source.getDateTime())
                .fromAccountId(source.getFromAccount() != null ? source.getFromAccount().getId() : null)
                .toAccountId(source.getToAccount() != null ? source.getToAccount().getId() : null)
                .build();
    }
}
