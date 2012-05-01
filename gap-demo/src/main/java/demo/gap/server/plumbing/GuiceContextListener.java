package demo.gap.server.plumbing;

import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * GuiceContextListener
 */
public class GuiceContextListener extends GuiceServletContextListener {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected Injector getInjector() {
        final Injector injector = Guice.createInjector(new GuiceProjectModule(),
            new GuiceServletModule());

        return injector;
    }

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent);

        // jul-to-slf4j bridge
        java.util.logging.LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();
    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        // jul-to-slf4j bridge
        SLF4JBridgeHandler.uninstall();

        super.contextDestroyed(servletContextEvent);
    }

}
