package main.entities;

import java.math.BigDecimal;

public class Account {
    Integer id;
    String name;
    BigDecimal balance;
    Integer userId;

    public Account() {
    }

    public Account(Integer id, String name,  BigDecimal balance, Integer userId) {
        this.name = name;
        this.id = id;
        this.balance = balance;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                ", userId=" + userId +
                '}';
    }
}
