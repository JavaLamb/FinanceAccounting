package main.servletUi;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/*")
public class MainServlet extends HttpServlet {
    @SuppressWarnings("rawtypes")
    private Map<String, Controller> routes = new HashMap<>();
    private Map<String, Controller> springControllers;
    ObjectMapper om = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        var springContext = (AnnotationConfigApplicationContext) servletContext.getAttribute("SpringContext");
        if (springContext == null) {
            throw new ServletException("SpringContext is null");
        }
        springControllers = springContext.getBeansOfType(Controller.class);
        for (Controller controller : springControllers.values()) {
            String url = controller.getClass().getAnnotation(WebController.class).value();
            if (url != null) {
                routes.put(url, controller);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        process(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        process(req, resp);
    }

    private void process(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String url = req.getRequestURI();
        Controller controller = routes.get(url);
        if (controller == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try {
            Object request = om.readValue(req.getInputStream(), controller.getRequestClass());
            Object response = controller.execute(request);
            om.writeValue(resp.getOutputStream(), response);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(e.getMessage());
        }
    }
}