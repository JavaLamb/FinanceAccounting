package main.entities;

import java.math.BigDecimal;

public class Account {
    Integer id;
    String name;
    BigDecimal balance;
    Integer userId;
    private AccountType accountType;

    public Account() {
    }


    public Account(String name, Integer userId, AccountType accountType) {
        this.name = name;
        this.userId = userId;
        this.accountType = accountType;
    }

    public Account(Integer id, String name, BigDecimal balance, Integer userId, AccountType accountType) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.userId = userId;
        this.accountType = accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getName() {
        return name;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public Integer getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                ", userId=" + userId +
                ", accountType=" + accountType +
                '}';
    }
}
