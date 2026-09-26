package main.converters;

import main.dto.Response.TransactionResponse;
import main.entities.Transaction;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TransactionToTransactionResponseConverter implements Converter<Transaction, TransactionResponse> {
    @Override
    public TransactionResponse convert(Transaction source) {
        TransactionResponse dto = new TransactionResponse()
                .setId(source.getId())
                .setTransactionType(source.getTransactionType())
                .setCategoryName(source.getTransactionCategory().getTransactionCategoryName())
                .setAmount(source.getAmount())
                .setDateTime(source.getDateTime());
        if (source.getFromAccount() != null) {
            dto.setFromAccountId(source.getFromAccount().getId());
        }
        if (source.getToAccount() != null) {
            dto.setToAccountId(source.getToAccount().getId());
        }
        return dto;
    }
}
