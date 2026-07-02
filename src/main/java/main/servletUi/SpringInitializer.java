package main.servletUi;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.springframework.boot.web.context.servlet.AnnotationConfigServletWebApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringInitializer implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        AnnotationConfigApplicationContext springContext = new AnnotationConfigApplicationContext();
        springContext.scan("main");
        springContext.refresh();
        servletContext.setAttribute("SpringContext", springContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        AnnotationConfigApplicationContext springContext = (AnnotationConfigApplicationContext) servletContext.getAttribute("SpringContext");
        if (springContext != null) springContext.close();
    }
}
