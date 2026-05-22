package main.entities;

import java.math.BigDecimal;

public class Transaction {
    int id;
    int fromAccountId;
    int toAccountId;
    int categoryId;
    BigDecimal amount;


    public int getId() {
        return id;
    }

    public int getFromAccountId() {
        return fromAccountId;
    }

    public int getToAccountId() {
        return toAccountId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
