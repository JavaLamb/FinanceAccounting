package main.servletUi.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import main.converters.UserToUserResponseConverter;
import main.entities.User;
import main.exceptions.AuthException;
import main.exceptions.RegistrationException;
import main.repositories.UserRepository;
import main.service.UserService;
import main.servletUi.dto.Request.LoginRequest;
import main.servletUi.dto.Request.RegiRequest;
import main.servletUi.dto.Response.UserResponse;
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

    @Transactional
    @PostMapping("/registration")
    public ResponseEntity<UserResponse> registration(@RequestBody RegiRequest request) {
        try {
            return ok(converter.convert(userService.registration(request)));
        } catch (RegistrationException e) {
            return status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authorization(@RequestBody LoginRequest request, HttpServletRequest req) {
        try {
            User user = userService.authorization(request);
            req.getSession().setAttribute("id", user.getId());
            return noContent().build();
        }catch(AuthException e){
            return status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
