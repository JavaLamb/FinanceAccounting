package main.entities;

public class TransactionCategory {
    int id;
    String transactionName;
    int userid;
    //Здесь мы  хотим дать возможность пользователю создавать кастомные категории, из-за чего нужно хранить userid.

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

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }
}
