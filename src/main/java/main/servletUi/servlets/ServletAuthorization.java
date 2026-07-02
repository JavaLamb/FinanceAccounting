package main.servletUi.servlets;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import main.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

public class ServletAuthorization extends HttpServlet {
    private UserService userService;

    //Получили из контекста сервисы
    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        var springContext = (AnnotationConfigApplicationContext) servletContext.getAttribute("SpringContext");
        if (springContext != null) {
            this.userService = springContext.getBean(UserService.class);
        } else {
            //Заглушка, надо как-то придумать обработку
            throw new ServletException("SpringContext is null");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
