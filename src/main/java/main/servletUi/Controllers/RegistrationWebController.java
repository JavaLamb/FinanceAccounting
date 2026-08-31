package main.servletUi.Controllers;

import lombok.AllArgsConstructor;
import main.service.UserService;
import main.servletUi.WebController;
import main.servletUi.Request.RegiRequest;
import main.servletUi.Response.RegiResponse;
import org.springframework.stereotype.Controller;

@AllArgsConstructor
@Controller("/registration")
public class RegistrationWebController implements WebController<RegiRequest, RegiResponse> {
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
        return RegiRequest.class;
    }
}
