package main.servletUi.core;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main.exceptions.AuthException;
import main.exceptions.RegistrationException;
import main.servletUi.dto.ApiResponse;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

@WebServlet("/*")
public class MainServlet extends HttpServlet {
    private Map<String, WebController> springControllers;
    ObjectMapper om = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        var springContext = (AnnotationConfigApplicationContext) servletContext.getAttribute("SpringContext");
        if (springContext == null) {
            throw new ServletException("SpringContext is null");
        }
        springControllers = springContext.getBeansOfType(WebController.class);
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
        WebController webController = springControllers.get(url);
        if (webController == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if(!url.equals("/login") && !url.equals("/registration")){
            HttpSession session = req.getSession();
            if(session == null || session.getAttribute("id") == null){
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                om.writeValue(resp.getOutputStream(), Map.of("error", "Необходимо авторизоваться"));
                return;
            }
        }
        try {
            Object request = om.readValue(req.getInputStream(), webController.getRequestClass());
            ApiResponse<?> response = webController.execute(request, req);
            resp.setStatus(response.status());
            om.writeValue(resp.getOutputStream(), response.response());
        } catch (AuthException ex) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            om.writeValue(resp.getOutputStream(), Map.of("error", ex.getMessage()));
        } catch (RegistrationException ex) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            om.writeValue(resp.getOutputStream(), Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(ex.getMessage());
        }
    }
}
