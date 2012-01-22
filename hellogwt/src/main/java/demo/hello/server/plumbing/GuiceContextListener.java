package demo.hello.server.plumbing;

import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.servlet.GuiceServletContextListener;

import demo.hello.server.cell.CellManager;
import demo.hello.shared.cell.CellEntity;

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

    static final String INJECTOR_NAME = Injector.class.getName();

    Injector getInjectorContext(final ServletContextEvent servletContextEvent) {
        final ServletContext servletContext = servletContextEvent.getServletContext();
        return (Injector) servletContext.getAttribute(INJECTOR_NAME);
    }

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent);

        final Injector injector = getInjectorContext(servletContextEvent);

        logger.info("start PersistService");
        injector.getInstance(PersistService.class).start();

        fillSomeCellEntity(injector);

        // jul-to-slf4j bridge
        java.util.logging.LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();
    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        logger.info("stop PersistService");
        getInjectorContext(servletContextEvent).getInstance(PersistService.class).stop();
        SLF4JBridgeHandler.uninstall();

        super.contextDestroyed(servletContextEvent);
    }

    /*
     * fillSomeCellEntity
     */
    void fillSomeCellEntity(final Injector injector) {

        final CellManager cellManager = injector.getInstance(CellManager.class);
        long cellCount = cellManager.countCell().longValue();

        logger.debug("cellCount: {}", cellCount);

        final long currentTimeMillis = System.currentTimeMillis();

        while (cellCount < 16) {
            try {
                final CellEntity cellEntity = new CellEntity(
                    RandomStringUtils.randomAlphanumeric(10), RandomUtils.nextDouble() * 100,
                    RandomUtils.nextBoolean(), new Date(new Double(Math.random()
                        * currentTimeMillis).longValue()));

                cellManager.createCell(cellEntity);

            } catch (final Exception e) {
                logger.error(e.getMessage(), e);
            }
            cellCount++;
        }
    }
}
