package demo.gap.server.mongo;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

import demo.gap.server.plumbing.mongo.MongoPersistModule;
import demo.gap.shared.domain.service.ActivityService;
import demo.gap.shared.domain.service.GapService;
import demo.gap.shared.domain.service.UserService;

/**
 * GuiceMongoModule
 */
public class GuiceMongoModule extends AbstractModule {

    @Override
    protected void configure() {
        // MongoPersistModule
        install(new MongoPersistModule());

        bind(GapService.class).to(MongoGapService.class).in(Singleton.class);
        bind(ActivityService.class).to(MongoActivityService.class).in(Singleton.class);
        bind(UserService.class).to(MongoUserService.class).in(Singleton.class);
    }
}
