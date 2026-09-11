package main.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import main.entities.User;
import main.exceptions.AuthException;
import main.exceptions.RegistrationException;
import main.repositories.UserRepository;
import main.dto.Request.LoginRequest;
import main.dto.Request.RegiRequest;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> findByEmailService(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean checkPassword(String password, User user) {
        String hash = user.getHashPassword();
        return BCrypt.checkpw(password, hash);
    }

    public User createUser(String email, String password) {
        User newUser = new User(email, password);
        return userRepository.save(newUser);
    }

    public boolean checkEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public User registration(RegiRequest request) {
        userRepository.findByEmail(request.getUsername())
                .ifPresent(_ -> {
                    throw new RegistrationException("Пользователь с данным email уже существует");
                });
        return userRepository.save(new User(request.getUsername(), BCrypt.hashpw(request.getPassword(), BCrypt.gensalt())));
    }

    public User authorization(LoginRequest req) {
        return userRepository.findByEmail(req.getUsername())
                .filter(user -> checkPassword(req.getPassword(), user))
                .orElseThrow(() -> new AuthException("Неправильный логин или пароль"));
    }

    public boolean isExistByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
