package main.servletUi;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@WebListener
public class SpringInitializer implements ServletContextListener {

    private AnnotationConfigApplicationContext context;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            context = new AnnotationConfigApplicationContext();

            context.scan("main");

            context.refresh();

            sce.getServletContext().setAttribute("SpringContext", context);

            System.out.println("Spring started successfully");

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if(this.context != null){
            this.context.close();
        }
        System.out.println("Spring context destroyed");
    }
}