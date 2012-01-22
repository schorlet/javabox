package demo.hello.server.plumbing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.server.ExceptionHandler;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;
import com.google.web.bindery.requestfactory.server.ServiceLayerDecorator;

/**
 * ProjectRequestFactoryServlet.
 * 
 * Guice injected in order to pass an instance of ProjectServiceLayerDecorator.
 */
public class ProjectRequestFactoryServlet extends RequestFactoryServlet {

    private static final long serialVersionUID = 2186240884950721814L;

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    ProjectRequestFactoryServlet(final ExceptionHandler exceptionHandler,
        final ServiceLayerDecorator serviceDecorator) {

        super(exceptionHandler, serviceDecorator);
        logger.debug("serviceDecorator: {}", serviceDecorator);
    }
}
