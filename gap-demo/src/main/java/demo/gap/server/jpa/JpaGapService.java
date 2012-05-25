package demo.gap.server.jpa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

import demo.gap.shared.domain.pojo.Gap;
import demo.gap.shared.domain.service.Filter;
import demo.gap.shared.domain.service.GapService;

/**
 * JpaGapService
 */
public class JpaGapService implements GapService {
    final Logger logger = LoggerFactory.getLogger(JpaGapService.class);

    final Provider<EntityManager> entityManager;

    @Inject
    JpaGapService(final Provider<EntityManager> provider) {
        this.entityManager = provider;
    }

    @Override
    public boolean isEmpty() {
        logger.trace("isEmpty");
        return size() == 0;
    }

    @Override
    public int size() {
        logger.trace("size");

        final CriteriaBuilder criteria = entityManager.get().getCriteriaBuilder();
        final CriteriaQuery<Long> query = criteria.createQuery(Long.class);

        // select count from
        final Root<GapEntity> gap = query.from(GapEntity.class);
        query.select(criteria.count(gap));

        // create query
        final TypedQuery<Long> typedQuery = entityManager.get().createQuery(query);
        return typedQuery.getSingleResult().intValue();
    }

    @Override
    public Set<String> getVersions() {
        logger.trace("getVersions");

        final CriteriaBuilder criteria = entityManager.get().getCriteriaBuilder();
        final CriteriaQuery<String> query = criteria.createQuery(String.class);

        // select from
        final Root<GapEntity> gap = query.from(GapEntity.class);
        query.select(gap.get(GapEntity_.version).as(String.class)).distinct(true);

        // order
        final List<Order> orders = new ArrayList<Order>();
        orders.add(criteria.asc(gap.get(GapEntity_.version)));
        query.orderBy(orders);

        // create query
        final TypedQuery<String> typedQuery = entityManager.get().createQuery(query);
        return new HashSet<String>(typedQuery.getResultList());
    }

    @Override
    public Set<Gap> getGaps() {
        return getByFilter(new Filter());
    }

    @Override
    public Gap getById(final String id) {
        logger.trace("getById {}", id);

        final Set<Gap> gaps = getByFilter(new Filter().byGapId(id));

        if (gaps.isEmpty()) return null;
        else return gaps.iterator().next();
    }

    @Override
    public Set<Gap> getByFilter(final Filter filter) {
        logger.trace("getByFilter {}", filter);

        final CriteriaBuilder criteria = entityManager.get().getCriteriaBuilder();
        final CriteriaQuery<GapEntity> query = criteria.createQuery(GapEntity.class);

        // select from
        final Root<GapEntity> gap = query.from(GapEntity.class);
        query.select(gap);

        // where
        if (filter.getGapId() != null) {
            query.where(criteria.equal(gap.get(GapEntity_.id), filter.getGapId()));

        }
        if (filter.getVersion() != null) {
            query.where(criteria.equal(gap.get(GapEntity_.version), filter.getVersion()));
        }

        // order
        query.orderBy(criteria.asc(gap.get(GapEntity_.version)));

        // create query from criteria query
        final TypedQuery<GapEntity> typedQuery = entityManager.get().createQuery(query);
        return JpaDomainUtil.buildGapSet(typedQuery.getResultList());
    }

    @Transactional
    @Override
    public void addAll(final Set<Gap> gaps) {
        logger.trace("addAll {}", gaps);

        final List<GapEntity> entities = JpaDomainUtil.buildGapEntities(gaps, entityManager.get());
        for (final GapEntity entity : entities) {
            entityManager.get().persist(entity);
        }
    }

    @Transactional
    @Override
    public void add(final Gap gap) {
        logger.trace("add {}", gap);

        final GapEntity entity = JpaDomainUtil.buildGapEntity(gap, entityManager.get());
        entityManager.get().persist(entity);
    }

    @Transactional
    @Override
    public boolean remove(final Gap gap) {
        final GapEntity entity = entityManager.get().find(GapEntity.class, gap.getId());
        if (entity != null) {
            logger.trace("remove {}", gap.getId());

            final List<ActivityEntity> activities = getActivities(entity);
            for (final ActivityEntity activity : activities) {
                entityManager.get().remove(activity);
            }

            entityManager.get().remove(entity);
            return true;

        } else {
            logger.warn("remove {} does not exists", gap.getId());
            return false;
        }
    }

    private List<ActivityEntity> getActivities(final GapEntity gapEntity) {
        logger.trace("getActivities {}", gapEntity);

        final CriteriaBuilder criteria = entityManager.get().getCriteriaBuilder();
        final CriteriaQuery<ActivityEntity> query = criteria.createQuery(ActivityEntity.class);

        // select from
        final Root<ActivityEntity> activity = query.from(ActivityEntity.class);
        query.select(activity);

        // where
        query.where(criteria.equal(activity.get(ActivityEntity_.gap), gapEntity));

        // create query from criteria query
        final TypedQuery<ActivityEntity> typedQuery = entityManager.get().createQuery(query);
        return typedQuery.getResultList();

    }

    @Transactional
    @Override
    public void clear() {
        logger.trace("clear");

        entityManager.get().createQuery("delete from ActivityEntity").executeUpdate();
        entityManager.get().createQuery("delete from GapEntity").executeUpdate();
        entityManager.get().clear();
    }

}
