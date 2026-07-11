package main.servletUi.Controllers;

import main.entities.User;
import main.service.UserService;
import main.servletUi.Controller;
import main.servletUi.Reques.LoginRequest;
import main.servletUi.Response.LoginResponse;
import main.servletUi.WebController;

@WebController("/login")
@org.springframework.stereotype.Controller
public class LoginController implements Controller<LoginRequest, LoginResponse> {
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public LoginResponse execute(LoginRequest request) {
        User user = userService.findByEmailService(request.getUsername());
        if (user != null && userService.checkPassword(request.getPassword(), user)) {
            return new LoginResponse(true);
        } else return new LoginResponse(false);
    }

    @Override
    public Class<LoginRequest> getRequestClass() {
        return LoginRequest.class;
    }
}
