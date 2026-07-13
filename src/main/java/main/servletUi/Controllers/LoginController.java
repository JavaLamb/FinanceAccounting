package main.servletUi.Controllers;

import lombok.AllArgsConstructor;
import main.service.UserService;
import main.servletUi.Controller;
import main.servletUi.Request.LoginRequest;
import main.servletUi.Response.LoginResponse;
import main.servletUi.WebController;

@AllArgsConstructor
@WebController("/login")
@org.springframework.stereotype.Controller
public class LoginController implements Controller<LoginRequest, LoginResponse> {
    private final UserService userService;

    @Override
    public LoginResponse execute(LoginRequest request) {
        return userService.findByEmailService(request.getUsername())
                .map(user -> userService.checkPassword(request.getPassword(), user))
                .map(LoginResponse::new)
                .orElse(new LoginResponse(false));
    }

    @Override
    public Class<LoginRequest> getRequestClass() {
        return LoginRequest.class;
    }
}
