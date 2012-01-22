package demo.hello.server.plumbing;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.web.bindery.requestfactory.server.ServiceLayer;
import com.google.web.bindery.requestfactory.server.ServiceLayerDecorator;
import com.google.web.bindery.requestfactory.shared.Locator;
import com.google.web.bindery.requestfactory.shared.ServiceLocator;

/**
 * ProjectServiceLayerDecorator.
 *
 * Users that intend to alter how RequestFactory interacts with the domain
 * environment can extend this type and provide it to
 * {@link ServiceLayer#create(ServiceLayerDecorator...)}. The methods defined in
 * this type will automatically delegate to the next decorator or the root
 * service object after being processed by{@code create()}.
 */
public class ProjectServiceLayerDecorator extends ServiceLayerDecorator {
    final Injector injector;

    @Inject
    ProjectServiceLayerDecorator(final Injector injector) {
        super();
        this.injector = injector;
    }

    /**
     * @return ProjectDomainLocator
     */
    @Override
    public <T extends Locator<?, ?>> T createLocator(final Class<T> clazz) {
        return injector.getInstance(clazz);
    }

    /**
     * @return ProjectServiceLocator
     */
    @Override
    public <T extends ServiceLocator> T createServiceLocator(final Class<T> clazz) {
        return injector.getInstance(clazz);
    }

}
