package demo.gap.server.mongo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jongo.Jongo;
import org.jongo.MongoCollection;

import demo.gap.shared.domain.pojo.Activity;
import demo.gap.shared.domain.pojo.Gap;
import demo.gap.shared.domain.pojo.User;

class JongoDomainUtil {

    static String toQueryString(final Set<String> keySet) {
        final Iterator<String> iterator = keySet.iterator();
        if (!iterator.hasNext()) return "{}";

        final StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            final String next = iterator.next();
            sb.append(next);
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }

        return sb.insert(0, '{').append('}').toString();
    }

    /*
     * users
     */

    static Set<User> buildUsers(final Iterable<UserEntity> entities) {
        final Set<User> users = new HashSet<User>();

        for (final UserEntity entity : entities) {
            users.add(new User(entity.getUser(), entity.getFirstname(), entity.getLastname()));
        }

        return users;
    }

    static UserEntity buildUserEntity(final String user, final MongoCollection collection) {
        return buildUserEntity(new User(user), collection);
    }

    private static UserEntity buildUserEntity(final User user, final MongoCollection collection) {
        UserEntity entity = collection.findOne("{user: #}", user.getUser()).as(UserEntity.class);

        if (entity == null) {
            entity = new UserEntity(user.getUser());
        }

        entity.setFirstname(user.getFirstname());
        entity.setLastname(user.getLastname());

        return entity;
    }

    /*
     * gaps
     */

    static Set<Gap> buildGaps(final Iterable<GapEntity> entities) {
        final Set<Gap> gaps = new HashSet<Gap>();

        for (final GapEntity entity : entities) {
            gaps.add(new Gap(entity.getId(), entity.getVersion(), entity.getDescription()));
        }

        return gaps;
    }

    static GapEntity buildGapEntity(final Gap gap, final MongoCollection collection) {
        GapEntity entity = collection.findOne("{id: #}", gap.getId()).as(GapEntity.class);

        if (entity == null) {
            entity = new GapEntity(gap.getId(), gap.getVersion());
        }

        entity.setVersion(gap.getVersion());
        entity.setDescription(gap.getDescription());

        return entity;
    }

    private static Gap buildGap(final GapEntity entity) {
        return new Gap(entity.getId(), entity.getVersion(), entity.getDescription());
    }

    /*
     * activities
     */

    static Set<Activity> buildActivities(final Iterable<ActivityEntity> entities) {
        final Set<Activity> activities = new HashSet<Activity>();

        for (final ActivityEntity entity : entities) {
            final Activity activity = buildActivity(entity);
            activities.add(activity);
        }

        return activities;
    }

    private static Activity buildActivity(final ActivityEntity entity) {
        final Gap gap = buildGap(entity.getGap());

        return new Activity(entity.getId(), gap, entity.getUser().getUser(), entity.getDay(),
            entity.getTime());
    }

    static ActivityEntity buildActivityEntity(final Activity activity, final Jongo jongo) {

        final MongoCollection activities = jongo.getCollection("activities");
        ActivityEntity entity = activities.findOne("{id: #}", activity.getId()).as(
            ActivityEntity.class);

        if (entity == null) {
            final MongoCollection gaps = jongo.getCollection("gaps");
            final MongoCollection users = jongo.getCollection("users");

            final GapEntity gapEntity = buildGapEntity(activity.getGap(), gaps);
            final UserEntity userEntity = buildUserEntity(activity.getUser(), users);

            entity = new ActivityEntity(activity.getId(), gapEntity, userEntity, activity.getDay(),
                activity.getTime());

        } else {
            entity.setDay(activity.getDay());
            entity.setTime(activity.getTime());
        }

        return entity;
    }

}
