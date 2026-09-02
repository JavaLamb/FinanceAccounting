package main.servletUi.Controllers;

import lombok.AllArgsConstructor;
import main.service.UserService;
import main.servletUi.core.WebController;
import main.servletUi.dto.ApiResponse;
import main.servletUi.dto.Request.LoginRequest;
import main.servletUi.dto.Response.LoginResponse;
import org.springframework.stereotype.Controller;

@AllArgsConstructor
@Controller("/login")
public class LoginWebController implements WebController<LoginRequest, LoginResponse> {
    private final UserService userService;

    @Override
    public ApiResponse<LoginResponse> execute(LoginRequest request) {
        return new ApiResponse<>(200,userService.webAuthorization(request));
    }

    @Override
    public Class<LoginRequest> getRequestClass() {
        return LoginRequest.class;
    }
}
