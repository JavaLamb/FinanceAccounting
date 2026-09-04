package main.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transaction_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "transaction_category_name")
    private String transactionCategoryName;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid", nullable = false)
    private User user;

    public TransactionCategory(String transactionCategoryName, Integer id) {
        this.transactionCategoryName = transactionCategoryName;
        this.id = id;
    }

    @Override
    public String toString() {
        return "Name ='" + transactionCategoryName + '\'';
    }
}
