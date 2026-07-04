package main.servletUi.servlets;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main.entities.User;
import main.service.UserService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
@WebServlet(value = "/login", loadOnStartup = 1)
public class ServletAuthorization extends HttpServlet {
    private UserService userService;
    HttpSession session ;

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
        req.getRequestDispatcher("login.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        User user = userService.findByEmailService(login);
        if (userService.checkPassword(password,user)) {
            session.setAttribute("CurrentUser", user);
            HttpSession session = req.getSession(true);
            session.setAttribute("currentUser", user);
            resp.sendRedirect("success.html");
        }
    }
}
