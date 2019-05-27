package ncherevko.moneytransfer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        ServletContextHandler ctx = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        ctx.setContextPath("/");

        server.setHandler(ctx);

        ServletHolder servletHolder = ctx.addServlet(ServletContainer.class, "/api/*");
        servletHolder.setInitParameter("jersey.config.server.provider.packages", "ncherevko.moneytransfer.api");

        try {
            log.info("About to start the application");
            server.start();
            server.join();
        } catch (Exception ex) {
            log.error("Error occurred while running application", ex);
            server.stop();
            server.destroy();
        }
    }
}
