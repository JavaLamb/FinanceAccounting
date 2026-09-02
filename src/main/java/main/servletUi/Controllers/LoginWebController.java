package main.servletUi.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
    public ApiResponse<LoginResponse> execute(LoginRequest request, HttpServletRequest req) {
        LoginResponse resp = userService.webAuthorization(request);
        req.getSession(false).setAttribute("id", resp.getId());
        return new ApiResponse<>(200,resp);
    }

    @Override
    public Class<LoginRequest> getRequestClass() {
        return LoginRequest.class;
    }
}
