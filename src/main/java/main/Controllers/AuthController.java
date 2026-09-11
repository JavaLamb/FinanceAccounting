package main.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.converters.UserToUserResponseConverter;
import main.entities.User;
import main.exceptions.AuthException;
import main.exceptions.RegistrationException;
import main.repositories.UserRepository;
import main.service.UserService;
import main.dto.Request.LoginRequest;
import main.dto.Request.RegiRequest;
import main.dto.Response.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.*;

@RequiredArgsConstructor
@RestController
public class AuthController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserToUserResponseConverter converter;

    @PostMapping("/registration")
    public ResponseEntity<UserResponse> registration(@RequestBody RegiRequest request) {
        try {
            return ok(converter.convert(userService.registration(request)));
        } catch (RegistrationException e) {
            return status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Void> authorization(@RequestBody LoginRequest loginRequest, HttpServletRequest req) {
        try {
            User user = userService.authorization(loginRequest);
            req.getSession().setAttribute("id", user.getId());
            return noContent().build();
        } catch (AuthException e) {
            return status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
