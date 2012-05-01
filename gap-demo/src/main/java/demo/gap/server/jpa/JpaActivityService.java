package demo.gap.server.jpa;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

import demo.gap.shared.domain.pojo.Activity;
import demo.gap.shared.domain.service.ActivityService;
import demo.gap.shared.domain.service.Filter;

/**
 * JpaActivityService
 */
public class JpaActivityService implements ActivityService {
    final Logger logger = LoggerFactory.getLogger(JpaActivityService.class);

    final Provider<EntityManager> entityManager;

    @Inject
    JpaActivityService(final Provider<EntityManager> provider) {
        this.entityManager = provider;
    }

    @Override
    public Set<Activity> getActivities() {
        return getByFilter(new Filter());
    }

    @Override
    public boolean isEmpty() {
        logger.debug("isEmpty");

        final CriteriaBuilder criteria = entityManager.get().getCriteriaBuilder();
        final CriteriaQuery<Long> query = criteria.createQuery(Long.class);

        // select count from
        final Root<ActivityEntity> root = query.from(ActivityEntity.class);
        query.select(criteria.count(root));

        // create query from criteria query
        final TypedQuery<Long> typedQuery = entityManager.get().createQuery(query);
        return typedQuery.getSingleResult() == 0;
    }

    @Override
    public Activity getById(final String id) {
        logger.debug("getById {}", id);

        final Set<Activity> activities = getByFilter(new Filter().byId(id));

        if (activities.isEmpty()) return null;
        else return activities.iterator().next();
    }

    public Set<Activity> getByGapId(final String gapid) {
        return getByFilter(new Filter().byGapId(gapid));
    }

    @Override
    public Set<Activity> getByFilter(final Filter filter) {
        logger.debug("getByFilter {}", filter);

        final CriteriaBuilder criteria = entityManager.get().getCriteriaBuilder();
        final CriteriaQuery<ActivityEntity> query = criteria.createQuery(ActivityEntity.class);

        // select from
        final Root<ActivityEntity> activity = query.from(ActivityEntity.class);
        query.select(activity);

        // fetch
        // activity.fetch(ActivityEntity_.gap);
        // activity.fetch(ActivityEntity_.user);

        // join
        final Join<ActivityEntity, GapEntity> gapJoin = activity.join(ActivityEntity_.gap);
        final Join<ActivityEntity, UserEntity> userJoin = activity.join(ActivityEntity_.user);

        // where
        Predicate conjunction = criteria.conjunction();

        if (filter.getId() != null) {
            final Predicate condition = criteria.equal(activity.get(ActivityEntity_.id),
                filter.getId());
            conjunction = criteria.and(conjunction, condition);

        } else {
            if (filter.getGapId() != null) {
                final Predicate condition = criteria.equal(gapJoin.get(GapEntity_.id),
                    filter.getGapId());
                conjunction = criteria.and(conjunction, condition);
            }

            if (filter.getUser() != null) {
                final Predicate condition = criteria.equal(userJoin.get(UserEntity_.user),
                    filter.getUser());
                conjunction = criteria.and(conjunction, condition);
            }

            if (filter.getVersion() != null) {
                final Predicate condition = criteria.equal(gapJoin.get(GapEntity_.version),
                    filter.getVersion());
                conjunction = criteria.and(conjunction, condition);
            }

            if (filter.getDay() != null) {
                final Predicate condition = criteria.equal(activity.get(ActivityEntity_.day),
                    filter.getDay());
                conjunction = criteria.and(conjunction, condition);

            } else {
                if (filter.getStartDate() != null) {
                    final Predicate condition = criteria.greaterThanOrEqualTo(
                        activity.get(ActivityEntity_.day), filter.getStartDate());
                    conjunction = criteria.and(conjunction, condition);
                }
                if (filter.getEndDate() != null) {
                    final Predicate condition = criteria.lessThanOrEqualTo(
                        activity.get(ActivityEntity_.day), filter.getEndDate());
                    conjunction = criteria.and(conjunction, condition);
                }
            }
        }

        query.where(conjunction);

        // order
        query.orderBy(criteria.desc(activity.get(ActivityEntity_.day)));

        // create query from criteria query
        final TypedQuery<ActivityEntity> typedQuery = entityManager.get().createQuery(query);
        return JpaDomainUtil.buildActivities(typedQuery.getResultList());
    }

    @Transactional
    @Override
    public void add(final Activity activity) {
        logger.trace("add {}", activity);

        final ActivityEntity entity = JpaDomainUtil.buildActivityEntity(activity,
            entityManager.get());
        entityManager.get().persist(entity);
    }

    @Transactional
    @Override
    public void addAll(final Set<Activity> activities) {
        logger.trace("addAll {}", activities);

        for (final Activity activity : activities) {
            final ActivityEntity entity = JpaDomainUtil.buildActivityEntity(activity,
                entityManager.get());
            entityManager.get().persist(entity);
        }
    }

    @Transactional
    @Override
    public boolean remove(final Activity activity) {
        final ActivityEntity entity = entityManager.get().find(ActivityEntity.class,
            activity.getId());

        if (entity != null) {
            logger.trace("remove {}", activity.getId());
            entityManager.get().remove(entity);
            return true;

        } else {
            logger.warn("remove {} does not exists", activity.getId());
            return false;
        }
    }

    @Transactional
    @Override
    public void clear() {
        logger.trace("clear");
        entityManager.get().createQuery("delete from ActivityEntity").executeUpdate();
        entityManager.get().clear();
    }

}
