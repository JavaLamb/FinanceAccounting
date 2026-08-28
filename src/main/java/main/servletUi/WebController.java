package main.servletUi;

public interface WebController<Req, Resp> {
    Resp execute(Req req);
    Class<Req> getRequestClass();
}
