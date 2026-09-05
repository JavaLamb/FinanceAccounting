package main.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id", referencedColumnName = "id")
    private Account fromAccount;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id", referencedColumnName = "id")
    private Account toAccount;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private TransactionCategory transactionCategory;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "date_time")
    private LocalDateTime dateTime;
    @Transient
    private TransactionType transactionType;

    public Transaction(TransactionType transactionType, Account fromAccount, Account toAccount, TransactionCategory transactionCategory, BigDecimal amount) {
        this.transactionType = transactionType;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.transactionCategory = transactionCategory;
        this.amount = amount;
    }

    public Transaction(TransactionType transactionType, Account AccountId, TransactionCategory transactionCategory, BigDecimal amount) {
        switch (transactionType) {
            case TransactionType.INCOME -> this.toAccount = AccountId;
            case TransactionType.EXPENSE -> this.fromAccount = AccountId;
        }
        this.transactionType = transactionType;
        this.transactionCategory = transactionCategory;
        this.amount = amount;
    }


    public Transaction(int id, Account fromAccount, Account toAccount, TransactionCategory transactionCategory, BigDecimal amount, LocalDateTime dateTime) {
        this.id = id;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.transactionCategory = transactionCategory;
        this.amount = amount;
        this.dateTime = dateTime;
    }

    public TransactionType getTransactionType() {
        if (fromAccount != null && toAccount != null) {
            return TransactionType.TRANSFER;
        } else if (fromAccount == null && toAccount != null) {
            return TransactionType.INCOME;
        } else {
            return TransactionType.EXPENSE;
        }
    }
}
