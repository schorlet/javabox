package demo.gap.server.resource;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;

import demo.gap.shared.domain.pojo.Activity;
import demo.gap.shared.domain.pojo.Gap;
import demo.gap.shared.domain.pojo.JerseyObject;
import demo.gap.shared.domain.pojo.User;
import demo.gap.shared.domain.pojo.Version;

/**
 * JAXBContextResolver
 */
@Provider
public class JAXBContextResolver implements ContextResolver<JAXBContext> {
    private static final Class<?>[] types = { Gap.class, Activity.class, User.class, Version.class,
        JerseyObject.class };

    private final JAXBContext context;

    public JAXBContextResolver() throws JAXBException {
        context = new JSONJAXBContext(JSONConfiguration.natural().humanReadableFormatting(true)
            .rootUnwrapping(true).build(), types);
    }

    public JAXBContext getContext(final Class<?> objectType) {
        for (final Class<?> type : types) {
            if (type == objectType) return context;
        }
        return null;
    }
}
