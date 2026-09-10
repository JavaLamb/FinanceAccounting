package main.servletUi.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import main.service.UserService;
import main.servletUi.dto.ApiResponse;
import main.servletUi.dto.Request.LoginRequest;
import main.servletUi.dto.Request.RegiRequest;
import main.servletUi.dto.Response.LoginResponse;
import main.servletUi.dto.Response.RegiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AuthController {
    private final UserService userService;

    @Transactional
    @PostMapping("/registration")
    public ApiResponse<RegiResponse> execute(@RequestBody RegiRequest regiRequest, HttpServletRequest req) {
        return new ApiResponse<>(201, userService.webRegistration(regiRequest));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> execute(@RequestBody LoginRequest request, HttpServletRequest req) {
        LoginResponse resp = userService.webAuthorization(request);
        req.getSession().setAttribute("id", resp.getId());
        return new ApiResponse<>(200, resp);
    }
}
