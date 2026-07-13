package main.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mindrot.jbcrypt.BCrypt;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    //Some fields of entitie which contain state of object
    Integer id;
    String email;
    String hashPassword;

    public User(String email, String password) {
        this.email = email;
        this.hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
