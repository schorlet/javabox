package demo.lucene1.web;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.lucene1.Commons;
import demo.lucene1.LuceneSupport;

/**
 * Application Lifecycle Listener.
 */
public final class ContextListener implements ServletContextListener {
    final Logger logger = LoggerFactory.getLogger(getClass());

    LuceneSupport luceneSupport;

    /**
     * close lucene support.
     */
    void close() {
        try {
            luceneSupport.close();
        } catch (final Exception e) {
            logger.error("destroy", e);
        }
    }

    /**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(final ServletContextEvent sce) {
        // add shutdown hook in case of brutal shutdown
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                close();
            }
        });

        try {
            final String indexDir = Commons.getLuceneIndex();
            luceneSupport = new LuceneSupport(indexDir, false);
            sce.getServletContext().setAttribute("luceneSupport", luceneSupport);

        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(final ServletContextEvent sce) {
        sce.getServletContext().removeAttribute("luceneSupport");
        close();
    }

}
