package main.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import main.converters.UserToUserResponseConverter;
import main.dto.Response.LoginResponse;
import main.security.CustomUserDetails;
import main.security.JwtService;
import main.service.UserService;
import main.dto.Request.LoginRequest;
import main.dto.Request.RegiRequest;
import main.dto.Response.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.validation.annotation.Validated;
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
    private final SecurityContextRepository securityContextRepository;
    private final JwtService jwtService;

    @PostMapping("/registration")
    public ResponseEntity<UserResponse> registration(@Validated @RequestBody RegiRequest request) {
        return ok(converter.convert(userService.registration(request)));
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authorization(
            @Validated @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.username(),
                        loginRequest.password()
                )
        );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(
                userDetails.getId().toString());

        return ResponseEntity.ok(new LoginResponse(accessToken));
    }
}
