package demo.hello;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.google.web.bindery.requestfactory.server.DefaultExceptionHandler;
import com.google.web.bindery.requestfactory.server.ExceptionHandler;
import com.google.web.bindery.requestfactory.server.ServiceLayer;
import com.google.web.bindery.requestfactory.server.ServiceLayerDecorator;
import com.google.web.bindery.requestfactory.server.SimpleRequestProcessor;
import com.google.web.bindery.requestfactory.server.testing.InProcessRequestTransport;
import com.google.web.bindery.requestfactory.shared.Locator;
import com.google.web.bindery.requestfactory.shared.ServiceLocator;
import com.google.web.bindery.requestfactory.vm.RequestFactorySource;

import demo.hello.server.CellClient2;
import demo.hello.server.SuperRepository;
import demo.hello.server.cell.CellManager;
import demo.hello.server.cell.CellRepository;
import demo.hello.server.plumbing.ProjectServiceLayerDecorator;
import demo.hello.server.plumbing.ProjectServiceLocator;
import demo.hello.shared.ProjectRequestFactory;
import demo.hello.shared.plumbing.ProjectDomainLocator;
import demo.hello.shared.plumbing.ProjectEntityFinder;

/**
 * GuiceTestModule
 */
public class GuiceTestModule extends AbstractModule {

    @Override
    protected void configure() {
        bindServer();
        bindRF();
        bindClient();
    }

    void bindClient() {
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(CellClient2.class).in(Singleton.class);
    }

    void bindRF() {
        // ExceptionHandler
        bind(ExceptionHandler.class).to(DefaultExceptionHandler.class).in(Singleton.class);

        // ServiceLayerDecorator
        bind(ServiceLayerDecorator.class).to(ProjectServiceLayerDecorator.class)
            .in(Singleton.class);

        // ServiceLocator
        bind(ServiceLocator.class).to(ProjectServiceLocator.class).in(Singleton.class);

        // DomainLocator
        bind(Locator.class).to(ProjectDomainLocator.class).in(Singleton.class);
        bind(ProjectEntityFinder.class).to(SuperRepository.class);

        // ExceptionHandler
        bind(ExceptionHandler.class).to(DefaultExceptionHandler.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    public ProjectRequestFactory createProjectRequestFactory(final EventBus eventBus,
        final ServiceLayerDecorator serviceLayerDecorator, final ExceptionHandler exceptionHandler) {

        // ServiceLayer
        final ServiceLayer serviceLayer = ServiceLayer.create(serviceLayerDecorator);

        final SimpleRequestProcessor processor = new SimpleRequestProcessor(serviceLayer);
        processor.setExceptionHandler(exceptionHandler);

        // InProcessRequestTransport
        final InProcessRequestTransport transport = new InProcessRequestTransport(processor);

        // RequestFactorySource.create
        final ProjectRequestFactory factory = RequestFactorySource
            .create(ProjectRequestFactory.class);

        factory.initialize(eventBus, transport);
        return factory;
    }

    void bindServer() {
        // JpaPersistModule
        install(new JpaPersistModule("dataStore"));

        bind(ValidatorFactory.class).toInstance(Validation.buildDefaultValidatorFactory());

        bind(CellRepository.class);
        bind(CellManager.class);
    }

}
