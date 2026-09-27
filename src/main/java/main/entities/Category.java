package main.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "transaction_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "transaction_name")
    private String transactionCategoryName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "userid",
            nullable = false,
            referencedColumnName = "id"
    )
    private User user;

    public Category(String transactionCategoryName, User user) {
        this.transactionCategoryName = transactionCategoryName;
        this.user = user;
    }

    @Override
    public String toString() {
        return "Name ='" + transactionCategoryName + '\'';
    }
}
