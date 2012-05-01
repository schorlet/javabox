package demo.gap.server.jpa;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.persist.jpa.JpaPersistModule;

import demo.gap.shared.domain.service.ActivityService;
import demo.gap.shared.domain.service.GapService;
import demo.gap.shared.domain.service.UserService;

/**
 * GuiceJpaModule
 */
public class GuiceJpaModule extends AbstractModule {

    @Override
    protected void configure() {
        // JpaPersistModule
        install(new JpaPersistModule("dataStore"));
        bind(ValidatorFactory.class).toInstance(Validation.buildDefaultValidatorFactory());

        bind(GapService.class).to(JpaGapService.class).in(Singleton.class);
        bind(ActivityService.class).to(JpaActivityService.class).in(Singleton.class);
        bind(UserService.class).to(JpaUserService.class).in(Singleton.class);
    }
}
