package main.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    int id;
    Integer fromAccountId;
    Integer toAccountId;
    int categoryId;
    TransactionType transactionType;
    BigDecimal amount;
    LocalDateTime dateTime;

    public Transaction(TransactionType transactionType, Integer fromAccountId, Integer toAccountId, int categoryId, BigDecimal amount) {
        this.transactionType = transactionType;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.categoryId = categoryId;
        this.amount = amount;
    }

    public Transaction(TransactionType transactionType, Integer AccountId, int categoryId, BigDecimal amount) {
        switch(transactionType) {
            case TransactionType.INCOME->this.toAccountId = AccountId;
            case TransactionType.EXPENSE->this.fromAccountId = AccountId;
        }
        this.transactionType = transactionType;
        this.categoryId = categoryId;
        this.amount = amount;
    }


    public Transaction(int id, Integer fromAccountId, Integer toAccountId, int categoryId, BigDecimal amount, LocalDateTime dateTime) {
        this.id = id;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.dateTime = dateTime;
    }

}
