package main.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCategory {
    int id;
    String transactionName;
    Integer userid;
    //Здесь мы  хотим дать возможность пользователю создавать кастомные категории, из-за чего нужно хранить userid.


    public TransactionCategory(String transactionName, Integer id) {
        this.transactionName = transactionName;
        this.id = id;
    }

    @Override
    public String toString() {
        return "Name='" + transactionName + '\'';
    }
}
