package main.servletUi.Controllers;

import lombok.AllArgsConstructor;
import main.service.UserService;
import main.servletUi.WebController;
import main.servletUi.Request.LoginRequest;
import main.servletUi.Response.LoginResponse;
import org.springframework.stereotype.Controller;

@AllArgsConstructor
@Controller("/login")
public class LoginWebController implements WebController<LoginRequest, LoginResponse> {
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
