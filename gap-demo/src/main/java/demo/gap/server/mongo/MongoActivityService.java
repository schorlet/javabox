package demo.gap.server.mongo;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.UnhandledException;
import org.jongo.Find;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

import demo.gap.shared.domain.pojo.Activity;
import demo.gap.shared.domain.service.ActivityService;
import demo.gap.shared.domain.service.Filter;

/**
 * MongoActivityService
 */
public class MongoActivityService implements ActivityService {
    final Logger logger = LoggerFactory.getLogger(MongoActivityService.class);

    final Provider<Jongo> jongo;

    @Inject
    MongoActivityService(final Provider<Jongo> provider) {
        this.jongo = provider;
    }

    MongoCollection getCollection() {
        return jongo.get().getCollection("activities");
    }

    @Override
    public Set<Activity> getActivities() {
        return getByFilter(new Filter());
    }

    @Override
    public boolean isEmpty() {
        logger.debug("isEmpty");

        final long count = getCollection().count("{}");
        return count == 0;
    }

    @Override
    public Activity getById(final String id) {
        logger.debug("getById {}", id);

        final Set<Activity> activities = getByFilter(new Filter().byId(id));

        if (activities.isEmpty()) return null;
        else return activities.iterator().next();
    }

    @Override
    public Set<Activity> getByGapId(final String gapid) {
        return getByFilter(new Filter().byGapId(gapid));
    }

    @Override
    public Set<Activity> getByFilter(final Filter filter) {
        logger.debug("getByFilter {}", filter);

        final Set<String> query = new LinkedHashSet<String>();

        if (filter.getId() != null) {
            query.add(String.format("id: '%s'", filter.getId()));

        } else {
            if (filter.getGapId() != null) {
                query.add(String.format("gap.id: '%s'", filter.getGapId()));
            }

            if (filter.getUser() != null) {
                query.add(String.format("user.user: '%s'", filter.getUser()));
            }

            if (filter.getVersion() != null) {
                query.add(String.format("gap.version: '%s'", filter.getVersion()));
            }

            if (filter.getStartDate() != null && filter.getEndDate() != null) {
                query.add(String.format("day: { $gte: %tQ, $lte: %tQ }", filter.getStartDate(),
                    filter.getEndDate()));

            } else if (filter.getStartDate() != null) {
                query.add(String.format("day: { $gte: %tQ }", filter.getStartDate()));

            } else if (filter.getEndDate() != null) {
                query.add(String.format("day: { $lte: %tQ }", filter.getEndDate()));
            }
        }

        final MongoCollection collection = getCollection();

        final String queryString = JongoDomainUtil.toQueryString(query);
        System.err.println(queryString);

        final Find find = collection.find(queryString).sort("{day: 1}");

        // create query from criteria query
        final Iterable<ActivityEntity> activities = find.as(ActivityEntity.class);
        return JongoDomainUtil.buildActivities(activities);
    }

    @Override
    public void addAll(final Set<Activity> activities) {
        logger.trace("addAll {}", activities);

        for (final Activity activity : activities) {
            add(activity);
        }
    }

    @Override
    public void add(final Activity activity) {
        logger.trace("add {}", activity);

        final MongoCollection collection = getCollection();

        try {
            ActivityEntity entity = collection.findOne("{id: #}", activity.getId()).as(
                ActivityEntity.class);

            if (entity == null) {
                final MongoCollection gaps = jongo.get().getCollection("gaps");
                final MongoCollection users = jongo.get().getCollection("users");

                final GapEntity gapEntity = JongoDomainUtil.buildGapEntity(activity.getGap(), gaps);
                final UserEntity userEntity = JongoDomainUtil.buildUserEntity(activity.getUser(),
                    users);

                entity = new ActivityEntity(activity.getId(), gapEntity, userEntity,
                    activity.getDay(), activity.getTime());
                collection.save(entity);

            } else {
                final String query = String.format("{id: '%s'}", activity.getId());
                final String modifier = String.format("{$set : {time: %1.2f}}", activity.getTime());

                collection.update(query, modifier);
            }

        } catch (final IOException e) {
            throw new UnhandledException(e);
        }
    }

    @Override
    public boolean remove(final Activity activity) {
        final MongoCollection collection = getCollection();
        final ActivityEntity entity = JongoDomainUtil.buildActivityEntity(activity, jongo.get());

        if (entity != null) {
            logger.trace("remove {}", activity.getId());
            final String query = String.format("{id: '%s'}", activity.getId());
            collection.remove(query);
            return true;

        } else {
            logger.warn("remove {} does not exists", activity.getId());
            return false;
        }
    }

    @Override
    public void clear() {
        logger.trace("clear");
        getCollection().drop();
    }

}
