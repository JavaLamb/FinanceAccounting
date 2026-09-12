package main.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "account")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedQueries({
        @NamedQuery(
                name = "Account.findAllAccountsByUserId",
                query = "select a from Account a where a.user.id = :userId"
        ),
        @NamedQuery(
                name = "Account.countAllAccountByUserId",
                query = "select count(a) from Account a where a.user.id = :userId"
        )
})
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false, referencedColumnName = "id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AccountType accountType;


    public Account(String name, User user, AccountType accountType) {
        this.name = name;
        this.user = user;
        this.accountType = accountType;
    }


    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                ", userId=" + user.getId() +
                ", accountType=" + accountType +
                '}';
    }
}
