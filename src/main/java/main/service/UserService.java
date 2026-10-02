package main.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import main.entities.User;
import main.exceptions.ValidationException;
import main.repositories.UserRepository;
import main.dto.Request.RegiRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

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
        if (userRepository.existsByEmail(request.username())) {
            throw new ValidationException(Map.of("username", List.of("Email is already taken")));
        }
        User newUser = new User(request.username(), encoder.encode(request.password()));
        return userRepository.save(newUser);
    }


    public boolean isExistByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
