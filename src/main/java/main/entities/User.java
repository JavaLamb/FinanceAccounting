package main.entities;

import org.mindrot.jbcrypt.BCrypt;

public class User {
    //Some fields of entitie which contain state of object
    Integer id;
    String email;
    String hashPassword;
    Account account1;
    Account account2;
    Account account3;
    Account account4;
    Account account5;

    public User(String email, String password) {
        this.email = email;
        this.hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public User(Integer id, String email, String hashPassword) {
        this.id = id;
        this.email = email;
        this.hashPassword = hashPassword;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
