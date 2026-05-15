package main.service;

import main.dao.UserDao;
import main.entities.User;
import org.mindrot.jbcrypt.BCrypt;

public class Authorization {
    UserDao uDao = new UserDao();

    public User auth(String email, String password) {
        User user = null;
        try {
            user = uDao.findByEmail(email);
        } catch (RuntimeException e) {
            System.out.println("Пользователь не найден");
        }
        if (user != null) {
            String hash = user.getHashPassword();
            if (BCrypt.checkpw(password, hash)) {
                return user;
            }
        }
        return null;
    }
}
