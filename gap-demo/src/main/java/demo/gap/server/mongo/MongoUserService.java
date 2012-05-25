package demo.gap.server.mongo;

import java.io.IOException;
import java.util.Set;

import org.apache.commons.lang.UnhandledException;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

import demo.gap.shared.domain.pojo.User;
import demo.gap.shared.domain.service.UserService;

/**
 * MongoUserService
 */
public class MongoUserService implements UserService {
    final Logger logger = LoggerFactory.getLogger(MongoUserService.class);

    final Provider<Jongo> jongo;

    @Inject
    MongoUserService(final Provider<Jongo> provider) {
        this.jongo = provider;
    }

    MongoCollection getCollection() {
        return jongo.get().getCollection("users");
    }

    @Override
    public boolean isEmpty() {
        logger.trace("isEmpty");

        final long count = getCollection().count("{}");
        return count == 0;
    }

    @Override
    public Set<User> getUsers() {
        logger.trace("getUsers");

        final Iterable<UserEntity> users = getCollection().find("{}").as(UserEntity.class);
        return JongoDomainUtil.buildUsers(users);
    }

    @Override
    public void clear() {
        logger.trace("clear");
        getCollection().drop();
    }

    @Override
    public void addAll(final Set<User> users) {
        logger.trace("addAll {}", users);

        for (final User user : users) {
            add(user);
        }
    }

    private void add(final User user) {
        logger.trace("add {}", user);

        final MongoCollection collection = getCollection();

        try {
            UserEntity entity = collection.findOne("{user: #}", user.getUser())
                .as(UserEntity.class);

            if (entity == null) {
                entity = new UserEntity(user.getUser(), user.getFirstname(), user.getLastname());
                collection.save(entity);

            } else {
                final String query = String.format("{user: '%s'}", user.getUser());
                final String modifier = String.format("{$set : {firstname: '%s', lastname: '%s'}}",
                    user.getFirstname(), user.getLastname());

                collection.update(query, modifier);
            }

        } catch (final IOException e) {
            throw new UnhandledException(e);
        }
    }

}
