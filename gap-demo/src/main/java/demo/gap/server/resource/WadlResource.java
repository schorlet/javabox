package demo.gap.server.resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;
import com.sun.jersey.server.wadl.ApplicationDescription;
import com.sun.jersey.server.wadl.WadlApplicationContext;
import com.sun.research.ws.wadl.Application;

/**
 * WadlResource
 */
@Singleton
@Path("application.wadl")
@Produces({ "application/vnd.sun.wadl+xml", MediaType.APPLICATION_XML })
public class WadlResource {
    static final Logger logger = LoggerFactory.getLogger(WadlResource.class);

    private static final String XML_HEADERS = "com.sun.xml.bind.xmlHeaders";

    private final WadlApplicationContext wadlContext;

    private byte[] wadlXmlRepresentation;

    public WadlResource(@Context final WadlApplicationContext wadlContext) {
        this.wadlContext = wadlContext;
    }

    @GET
    public synchronized Response getWadl(@Context final UriInfo uriInfo) {
        if (wadlXmlRepresentation == null) {
            final ApplicationDescription description = wadlContext.getApplication(uriInfo);
            final Application application = description.getApplication();

            try {
                final Marshaller marshaller = wadlContext.getJAXBContext().createMarshaller();

                marshaller.setProperty(XML_HEADERS,
                    "<?xml-stylesheet type='text/xsl' href='/wadl_documentation-2009-02.xsl'?>");
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                final ByteArrayOutputStream os = new ByteArrayOutputStream();
                marshaller.marshal(application, os);
                wadlXmlRepresentation = os.toByteArray();
                os.close();

            } catch (final Exception e) {
                logger.warn("Could not marshal wadl Application.", e);
                return javax.ws.rs.core.Response.ok(application).build();
            }
        }

        return Response.ok(new ByteArrayInputStream(wadlXmlRepresentation)).build();
    }
}
