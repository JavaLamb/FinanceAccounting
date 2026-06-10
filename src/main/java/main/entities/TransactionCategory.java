package main.entities;

public class TransactionCategory {
    int id;
    String transactionName;
    Integer userid;
    //Здесь мы  хотим дать возможность пользователю создавать кастомные категории, из-за чего нужно хранить userid.


    public TransactionCategory(String transactionName, Integer id) {
        this.transactionName = transactionName;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    @Override
    public String toString() {
        return "Name='" + transactionName + '\'';
    }
}
