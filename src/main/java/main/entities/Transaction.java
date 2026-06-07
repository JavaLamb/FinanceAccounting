package main.entities;

import java.math.BigDecimal;

public class Transaction {
    int id;
    int fromAccountId;
    int toAccountId;
    TransactionType categoryId;
    BigDecimal amount;
    TransactionCategory category;

    public TransactionCategory getCategory() {
        return category;
    }

    public int getId() {
        return id;
    }

    public int getFromAccountId() {
        return fromAccountId;
    }

    public int getToAccountId() {
        return toAccountId;
    }

    public TransactionType getCategoryId() {
        return categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setCategoryId(TransactionType categoryId) {
        this.categoryId = categoryId;
    }
}
