package demo.gap.server.resource;

import javax.ws.rs.ApplicationPath;

import com.sun.jersey.api.core.PackagesResourceConfig;

/**
 * GapApplication
 * http://localhost:8080/resource/application.wadl
 */
@ApplicationPath("resource")
public class GapApplication extends PackagesResourceConfig {

    public GapApplication() {
        super(GapApplication.class.getPackage().getName());
    }
}
