package demo.gap.server.plumbing;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.persist.jpa.JpaPersistModule;

import demo.gap.server.jpa.JpaActivityService;
import demo.gap.server.jpa.JpaGapService;
import demo.gap.server.jpa.JpaUserService;
import demo.gap.shared.domain.service.ActivityService;
import demo.gap.shared.domain.service.GapService;
import demo.gap.shared.domain.service.UserService;

/**
 * GuiceProjectModule
 */
public class GuiceProjectModule extends AbstractModule {

    @Override
    protected void configure() {
        // JpaPersistModule
        install(new JpaPersistModule("dataStore"));
        bind(ValidatorFactory.class).toInstance(Validation.buildDefaultValidatorFactory());

        bind(GapService.class).to(JpaGapService.class).in(Singleton.class);
        bind(ActivityService.class).to(JpaActivityService.class).in(Singleton.class);
        bind(UserService.class).to(JpaUserService.class).in(Singleton.class);

        // bind(GapService.class).to(MemGapService.class).in(Singleton.class);
        // bind(ActivityService.class).to(MemActivityService.class).in(Singleton.class);
        // bind(UserService.class).to(MemUserService.class).in(Singleton.class);
    }

}
