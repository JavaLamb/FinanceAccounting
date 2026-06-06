package main.service;

import main.dao.UserDao;
import main.entities.User;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final UserDao userDao;
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User findByEmailService(String email){
        return userDao.findByEmail(email);
    }

    public boolean checkPassword(String password, User user){
        String hash = user.getHashPassword();
        return BCrypt.checkpw(password, hash);
    }
}
