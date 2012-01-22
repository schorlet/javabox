package demo.hello.server.plumbing;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;
import com.google.web.bindery.requestfactory.shared.ServiceLocator;

import demo.hello.server.cell.CellManager;
import demo.hello.shared.ProjectRequestFactory;

/**
 * ProjetServiceLocator.
 * 
 * A ServiceLocator provides instances of a type specified by a {@link Service}
 * when {@link Request} methods declared in a {@link RequestContext}are mapped
 * onto instance (non-static) methods.
 * <p>
 * ServiceLocator subtypes must be default instantiable (i.e. public static
 * types with a no-arg constructor). Instances of ServiceLocators may be
 * retained and reused by the RequestFactory service layer.
 * 
 * @see Service#locator()
 */
public class ProjectServiceLocator implements ServiceLocator {

    private final Injector injector;

    @Inject
    ProjectServiceLocator(final Injector injector) {
        this.injector = injector;
    }

    /**
     * Used by {@link ProjectRequestFactory.CellRequest} in order to locate 
     * {@link CellManager}.
     */
    @Override
    public Object getInstance(final Class<?> clazz) {
        return injector.getInstance(clazz);
    }

}
