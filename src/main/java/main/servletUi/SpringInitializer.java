package main.servletUi;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@WebListener
public class SpringInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            AnnotationConfigApplicationContext context =
                    new AnnotationConfigApplicationContext();

            context.scan("main");

            context.refresh();

            sce.getServletContext().setAttribute("SpringContext", context);

            System.out.println("Spring started successfully");

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}