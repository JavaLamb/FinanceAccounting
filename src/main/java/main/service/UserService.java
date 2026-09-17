package main.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import main.entities.User;
import main.exceptions.RegistrationException;
import main.repositories.UserRepository;
import main.dto.Request.RegiRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public Optional<User> findByEmailService(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean checkPassword(String password, User user) {
        String hash = user.getHashPassword();
        return encoder.matches(password, hash);
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
        return userRepository.save(new User(request.getUsername(), encoder.encode(request.getPassword())));
    }


    public boolean isExistByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
