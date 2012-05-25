package demo.gap.server.plumbing.mongo;

import org.apache.commons.lang.UnhandledException;
import org.jongo.Jongo;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.UnitOfWork;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;

public class MongoPersistService implements Provider<Jongo>, PersistService, UnitOfWork {
    private final MongoURI mongoUri;

    private Jongo jongo;

    @Inject
    public MongoPersistService(@MongoUri final String uri) {
        mongoUri = new MongoURI(uri);
    }

    @Override
    public void start() {
        try {
            final Mongo mongo = mongoUri.connect();
            final DB db = mongo.getDB(mongoUri.getDatabase());

            if (mongoUri.getUsername() != null && mongoUri.getPassword() != null) {
                db.authenticate(mongoUri.getUsername(), mongoUri.getPassword());
            }

            jongo = new Jongo(db);

        } catch (final Exception e) {
            throw new UnhandledException(e);
        }
    }

    @Override
    public void stop() {
        jongo.getDatabase().getMongo().close();
        jongo = null;
    }

    @Override
    public Jongo get() {
        return jongo;
    }

    @Override
    public void begin() {
        // nothing to do
    }

    @Override
    public void end() {
        // nothing to do
    }
}
