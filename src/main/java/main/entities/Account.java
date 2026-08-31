package main.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    Integer id;
    String name;
    BigDecimal balance;
    Integer userId;
    private AccountType accountType;


    public Account(String name, Integer userId, AccountType accountType, BigDecimal balance) {
        this.balance = balance;
        this.name = name;
        this.userId = userId;
        this.accountType = accountType;
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
