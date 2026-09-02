package main.service;

import main.dao.UserDao;
import main.entities.User;
import main.exceptions.AuthException;
import main.exceptions.RegistrationException;
import main.servletUi.dto.Request.LoginRequest;
import main.servletUi.dto.Request.RegiRequest;
import main.servletUi.dto.Response.LoginResponse;
import main.servletUi.dto.Response.RegiResponse;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Optional<User> findByEmailService(String email) {
        return userDao.findByEmail(email);
    }

    public boolean checkPassword(String password, User user) {
        String hash = user.getHashPassword();
        return BCrypt.checkpw(password, hash);
    }

    public User createUser(String email, String password) {
        User newUser = new User(email, password);
        return userDao.insert(newUser);
    }

    public boolean checkEmail(String email) {
        return userDao.findByEmail(email).isPresent();
    }
    public RegiResponse webRegistration(RegiRequest req){
        userDao.findByEmail(req.getUsername())
                .ifPresent(_ -> {throw new RegistrationException("Пользователь с данным email уже существует");});
        User user = userDao.insert(new User(req.getUsername(), req.getPassword()));
        return new RegiResponse(user.getId(),user.getEmail());
    }

    public LoginResponse webAuthorization(LoginRequest req){
        return userDao.findByEmail(req.getUsername())
                .filter(user -> checkPassword(req.getPassword(), user))
                .map(user -> new LoginResponse(user.getId(),user.getEmail()))
                .orElseThrow(()-> new AuthException("Неправильный логин или пароль"));
    }
}
