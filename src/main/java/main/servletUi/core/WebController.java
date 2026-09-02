package main.servletUi.core;

import main.servletUi.dto.ApiResponse;

public interface WebController<Req, Resp> {
    ApiResponse<Resp> execute(Req req);
    Class<Req> getRequestClass();
}
