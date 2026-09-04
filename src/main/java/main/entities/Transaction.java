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
    @Column(name = "from_account_id")
    private Integer fromAccountId;
    @Column(name = "to_account_id")
    private Integer toAccountId;
    @Column(name = "category_id")
    private int categoryId;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "date_time")
    private LocalDateTime dateTime;
    @Transient
    private TransactionType transactionType;

    public Transaction(TransactionType transactionType, Integer fromAccountId, Integer toAccountId, int categoryId, BigDecimal amount) {
        this.transactionType = transactionType;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.categoryId = categoryId;
        this.amount = amount;
    }

    public Transaction(TransactionType transactionType, Integer AccountId, int categoryId, BigDecimal amount) {
        switch (transactionType) {
            case TransactionType.INCOME -> this.toAccountId = AccountId;
            case TransactionType.EXPENSE -> this.fromAccountId = AccountId;
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

    public TransactionType getTransactionType() {
        if (fromAccountId != null && toAccountId != null) {
            return TransactionType.TRANSFER;
        } else if (fromAccountId == null && toAccountId != null) {
            return TransactionType.INCOME;
        } else {
            return TransactionType.EXPENSE;
        }
    }
}
