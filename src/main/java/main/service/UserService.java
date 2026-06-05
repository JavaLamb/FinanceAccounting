package main.service;

import main.dao.UserDao;
import main.entities.User;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    private final UserDao userDao;
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }
}
