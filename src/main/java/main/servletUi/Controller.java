package main.servletUi;

public interface Controller<Req, Resp> {
    Resp execute(Req req);
    Class<Req> getRequestClass();
}
