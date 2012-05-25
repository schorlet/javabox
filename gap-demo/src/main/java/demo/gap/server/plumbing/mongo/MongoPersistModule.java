package demo.gap.server.plumbing.mongo;

import org.jongo.Jongo;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.UnitOfWork;

public class MongoPersistModule extends AbstractModule {

    public static final String DEFAULT_URI = "mongodb://127.0.0.1:27017/gap-demo";

    private final String mongoUri;

    public MongoPersistModule() {
        this.mongoUri = DEFAULT_URI;
    }

    public MongoPersistModule(final String mongoUri) {
        if (mongoUri == null || mongoUri.length() == 0)
            throw new IllegalArgumentException("mongoUri must be a non-empty string.");
        this.mongoUri = mongoUri;
    }

    @Override
    protected void configure() {
        bindConstant().annotatedWith(MongoUri.class).to(mongoUri);

        bind(MongoPersistService.class).in(Singleton.class);
        bind(PersistService.class).to(MongoPersistService.class);
        bind(UnitOfWork.class).to(MongoPersistService.class);

        bind(Jongo.class).toProvider(MongoPersistService.class);
    }

}
