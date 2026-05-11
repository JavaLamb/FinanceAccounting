package MainPackage;

public class User {
    private String email;
    private String hashPassword;
    private String id;

    public User() {
    }

    public User(String id, String email, String hashPassword) {
        this.email = email;
        this.hashPassword = hashPassword;
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getHashPassword() {
        return hashPassword;
    }
}
