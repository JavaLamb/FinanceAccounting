package main.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import main.converters.UserToUserResponseConverter;
import main.exceptions.RegistrationException;
import main.service.UserService;
import main.dto.Request.LoginRequest;
import main.dto.Request.RegiRequest;
import main.dto.Response.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.*;

@RequiredArgsConstructor
@RestController
public class AuthController {
    private final UserService userService;
    private final UserToUserResponseConverter converter;
    private final AuthenticationManager authenticationManager;

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
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        return noContent().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest req){
        req.getSession().invalidate();
        return noContent().build();
    }
}
