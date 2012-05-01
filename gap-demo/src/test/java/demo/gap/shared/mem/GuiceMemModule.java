package demo.gap.shared.mem;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import demo.gap.shared.domain.service.ActivityService;
import demo.gap.shared.domain.service.GapService;
import demo.gap.shared.domain.service.UserService;

/**
 * GuiceMemModule
 */
public class GuiceMemModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(GapService.class).to(MemGapService.class).in(Singleton.class);
        bind(ActivityService.class).to(MemActivityService.class).in(Singleton.class);
        bind(UserService.class).to(MemUserService.class).in(Singleton.class);
    }
}
