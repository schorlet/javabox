package demo.gap.server.jpa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import demo.gap.shared.domain.pojo.Activity;
import demo.gap.shared.domain.pojo.Gap;
import demo.gap.shared.domain.pojo.User;

/**
 * JpaDomainUtil
 */
class JpaDomainUtil {

    /*
     * users
     */

    static Set<User> buildUsers(final List<UserEntity> entities) {
        final Set<User> users = new HashSet<User>(entities.size());

        for (final UserEntity entity : entities) {
            users.add(new User(entity.getUser(), entity.getFirstname(), entity.getLastname()));
        }

        return users;
    }

    private static UserEntity buildUserEntity(final String user, final EntityManager entityManager) {
        return buildUserEntity(new User(user), entityManager);
    }

    static UserEntity buildUserEntity(final User user, final EntityManager entityManager) {
        UserEntity entity = entityManager.find(UserEntity.class, user.getUser());

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

    static Set<Gap> buildGapSet(final List<GapEntity> entities) {
        final Set<Gap> gaps = new HashSet<Gap>();

        for (final GapEntity entity : entities) {
            gaps.add(buildGap(entity));
        }

        return gaps;
    }

    private static Gap buildGap(final GapEntity entity) {
        return new Gap(entity.getId(), entity.getVersion(), entity.getDescription());
    }

    static List<GapEntity> buildGapEntities(final Set<Gap> gaps, final EntityManager entityManager) {

        final List<GapEntity> entities = new ArrayList<GapEntity>();

        for (final Gap gap : gaps) {
            entities.add(buildGapEntity(gap, entityManager));
        }

        return entities;
    }

    static GapEntity buildGapEntity(final Gap gap, final EntityManager entityManager) {
        GapEntity entity = entityManager.find(GapEntity.class, gap.getId());

        if (entity == null) {
            entity = new GapEntity(gap.getId(), gap.getVersion());
        }

        entity.setVersion(gap.getVersion());
        entity.setDescription(gap.getDescription());

        return entity;
    }

    /*
     * activities
     */

    static Set<Activity> buildActivities(final List<ActivityEntity> entities) {
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

    static ActivityEntity buildActivityEntity(final Activity activity,
        final EntityManager entityManager) {

        final GapEntity gapEntity = entityManager.find(GapEntity.class, activity.getGap().getId());
        return buildActivityEntity(gapEntity, activity, entityManager);
    }

    private static ActivityEntity buildActivityEntity(final GapEntity gapEntity,
        final Activity activity, final EntityManager entityManager) {

        ActivityEntity activityEntity = entityManager.find(ActivityEntity.class, activity.getId());

        if (activityEntity == null) {
            activityEntity = new ActivityEntity(activity.getId(), gapEntity, buildUserEntity(
                activity.getUser(), entityManager), activity.getDay(), activity.getTime());

        } else {
            activityEntity.setDay(activity.getDay());
            activityEntity.setTime(activity.getTime());
        }

        return activityEntity;
    }

}
