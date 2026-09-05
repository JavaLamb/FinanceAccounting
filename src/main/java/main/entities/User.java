package main.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NamedQueries({
        @NamedQuery(
                name ="User.findByEmail",
                query = "select u from User u where u.email = :email"
        )
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "email", unique = true, nullable = false)
    private String email;
    @Column(name = "password", nullable = false)
    private String hashPassword;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Account> accounts;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TransactionCategory> categories;

    public User(String email, String password) {
        this.email = email;
        this.hashPassword = password;
    }
}
