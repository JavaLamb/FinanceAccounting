package main.servletUi.Controllers;

import lombok.AllArgsConstructor;
import main.entities.User;
import main.service.UserService;
import main.servletUi.Controller;
import main.servletUi.Request.RegiRequest;
import main.servletUi.Response.RegiResponse;
import main.servletUi.WebController;

@AllArgsConstructor
@WebController("/registration")
@org.springframework.stereotype.Controller
public class RegistrationController implements Controller<RegiRequest, RegiResponse> {
    private final UserService userService;

    @Override
    public RegiResponse execute(RegiRequest regiRequest) {
        if (userService.checkEmail(regiRequest.getUsername())) {
            return new RegiResponse();
        } else {
            userService.createUser(regiRequest.getUsername(), regiRequest.getPassword());
            return new RegiResponse(true);
        }
    }

    @Override
    public Class<RegiRequest> getRequestClass() {
        return null;
    }
}
