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
//        userService.findByEmailService(request.getUsername())
//                .map(user -> userService.checkPassword(request.getPassword(), user))
//                .map(LoginResponse::new)
//                .orElse(new LoginResponse(false));
        //ждем из сервиса сущность, переупаковываем её в dto loginresponse, выставляем нужный status и возвращаем
        //все упакованное в apiresponse +- такой flow.
        return new ApiResponse<>(200,userService.webAuthorization(request));
    }

    @Override
    public Class<LoginRequest> getRequestClass() {
        return LoginRequest.class;
    }
}
