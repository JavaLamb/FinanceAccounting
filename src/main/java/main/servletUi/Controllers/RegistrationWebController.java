package main.servletUi.Controllers;

import lombok.AllArgsConstructor;
import main.service.UserService;
import main.servletUi.core.WebController;
import main.servletUi.dto.ApiResponse;
import main.servletUi.dto.Request.RegiRequest;
import main.servletUi.dto.Response.RegiResponse;
import org.springframework.stereotype.Controller;

@AllArgsConstructor
@Controller("/registration")
public class RegistrationWebController implements WebController<RegiRequest, RegiResponse> {
    private final UserService userService;

    @Override
    public ApiResponse<RegiResponse> execute(RegiRequest regiRequest) {
        return new ApiResponse<>(201, userService.webRegistration(regiRequest));
    }

    @Override
    public Class<RegiRequest> getRequestClass() {
        return RegiRequest.class;
    }
}
