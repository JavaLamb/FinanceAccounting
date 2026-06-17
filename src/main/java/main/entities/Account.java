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


    public Account(String name, Integer userId, AccountType accountType, BigDecimal balance) {
        this.balance = balance;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setId(Integer id) {
        this.id = id;
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
