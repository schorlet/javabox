package demo.gap.server.plumbing;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.servlet.ServletModule;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import demo.gap.server.resource.GapApplication;

public class GuiceServletModule extends ServletModule {

    @Override
    public void configureServlets() {
        filter("/resource/*").through(PersistFilter2.class);
        bindJersey();
    }

    void bindJersey() {
        install(new JerseyServletModule());

        // Route all requests through GuiceContainer
        filter("/resource/*").through(GuiceContainer.class, initParams());
    }

    Map<String, String> initParams() {
        final Map<String, String> params = new HashMap<String, String>();

        params.put(ServletContainer.APPLICATION_CONFIG_CLASS, GapApplication.class.getName());

        params.put(ServletContainer.PROPERTY_FILTER_CONTEXT_PATH, "/resource/");
        params.put(ServletContainer.PROPERTY_WEB_PAGE_CONTENT_REGEX,
            "/(css|img|js|(WEB-INF/resource))/.*");

        params.put(ServletContainer.JSP_TEMPLATES_BASE_PATH, "/WEB-INF/resource");
        params.put(ResourceConfig.FEATURE_IMPLICIT_VIEWABLES, "true");

        params.put(ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS,
            "com.sun.jersey.api.container.filter.LoggingFilter");
        params.put(ResourceConfig.PROPERTY_CONTAINER_RESPONSE_FILTERS,
            "com.sun.jersey.server.linking.LinkFilter");
        // com.sun.jersey.api.container.filter.LoggingFilter

        // params.put(ResourceConfig.FEATURE_TRACE, "true");

        return params;
    }

}
