package MainPackage;

public class Account {
    String id;
    String name;
    String balance;
    String userid;

    public Account() {
    }

    public Account(String id, String name, String balance, String userid) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.userid = userid;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", balance='" + balance + '\'' +
                ", userid='" + userid + '\'' +
                '}';
    }
}
