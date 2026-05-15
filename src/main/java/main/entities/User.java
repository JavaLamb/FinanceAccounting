package main.entities;

public class User {
    //Some fields of entitie which contain state of object
    Integer id;
    String email;
    String hashPassword;

    public User() {
    }

    public User(Integer id, String email, String hashPassword) {
        this.id = id;
        this.email = email;
        this.hashPassword = hashPassword;
    }

    public String getHashPassword() {
        return hashPassword;
    }
}
