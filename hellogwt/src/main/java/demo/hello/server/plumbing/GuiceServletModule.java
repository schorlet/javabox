package demo.hello.server.plumbing;

import com.google.inject.servlet.ServletModule;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;

/**
 * GuiceServletModule
 */
public class GuiceServletModule extends ServletModule {

    @Override
    public void configureServlets() {
        serve("/gwtRequest").with(RequestFactoryServlet.class);
    }

}
