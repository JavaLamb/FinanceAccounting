package main.servletUi.core;

import com.sun.net.httpserver.Request;
import jakarta.servlet.http.HttpServletRequest;
import main.servletUi.dto.ApiResponse;

public interface WebController<Req, Resp> {
    ApiResponse<Resp> execute(Req req, HttpServletRequest request);
    Class<Req> getRequestClass();
}
