package demo.hello.server.plumbing;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.web.bindery.requestfactory.server.DefaultExceptionHandler;
import com.google.web.bindery.requestfactory.server.ExceptionHandler;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;
import com.google.web.bindery.requestfactory.server.ServiceLayerDecorator;
import com.google.web.bindery.requestfactory.shared.Locator;
import com.google.web.bindery.requestfactory.shared.ServiceLocator;

import demo.hello.server.SuperRepository;
import demo.hello.server.cell.CellManager;
import demo.hello.server.cell.CellRepository;
import demo.hello.shared.plumbing.ProjectDomainLocator;
import demo.hello.shared.plumbing.ProjectEntityFinder;

/**
 * GuiceProjectModule
 */
public class GuiceProjectModule extends AbstractModule {

    @Override
    protected void configure() {
        bindServer();
        bindRF();
    }

    void bindRF() {
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

        // RequestFactoryServlet
        bind(RequestFactoryServlet.class).to(ProjectRequestFactoryServlet.class)
            .in(Singleton.class);
    }

    void bindServer() {
        // JpaPersistModule
        install(new JpaPersistModule("dataStore"));

        bind(ValidatorFactory.class).toInstance(Validation.buildDefaultValidatorFactory());

        bind(CellRepository.class);
        bind(CellManager.class);
    }

}
