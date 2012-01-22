package demo.hello.server;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.inject.Inject;

import demo.hello.shared.FilterConstraint;
import demo.hello.shared.FilterConstraintOpEnum;
import demo.hello.shared.SortConstraint;
import demo.hello.shared.SuperEntity;
import demo.hello.shared.plumbing.ProjectEntityFinder;

/**
 * SuperRepository
 */
public class SuperRepository<T extends SuperEntity> implements ProjectEntityFinder {

    final EntityManager entityManager;

    @Inject
    protected SuperRepository(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * @param domainObject the domainObject
     */
    public void persist(final T domainObject) {
        entityManager.persist(domainObject);
    }

    /**
     * @param domainObject the domainObject
     */
    public T merge(final T domainObject) {
        return entityManager.merge(domainObject);
    }

    /**
     * @param domainObject the domainObject
     */
    public void remove(final T domainObject) {
        final Object merge = merge(domainObject);
        entityManager.remove(merge);
    }

    public int clear(final Class<? extends SuperEntity> domainClass) {
        final Query query = entityManager.createQuery("delete from " + domainClass.getSimpleName());
        return query.executeUpdate();
    }

    @Override
    public SuperEntity create(final Class<? extends SuperEntity> domainClass) {
        try {
            return domainClass.newInstance();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param domainClass the type of the returned domainObject
     * @param id the id of the domainObject
     * @return a domainObject
     */
    @Override
    public SuperEntity find(final Class<? extends SuperEntity> domainClass, final Integer id) {
        return entityManager.find(domainClass, id);
    }

    /**
     * @param domainClass the type of the returned domainObject
     * @return a list of domainObject
     */
    public List<T> list(final Class<T> domainClass) {
        return list(domainClass, 0, 100);
    }

    /**
     * @param jpql the jpa sql
     * @param domainClass the type of the returned list
     * @param args the jpql args
     * @return a list of domainObject
     */
    public List<T> list(final Class<T> domainClass, final String jpql, final Object... args) {
        final TypedQuery<T> query = entityManager.createQuery(jpql, domainClass);

        for (int i = 0; i < args.length; i++) {
            query.setParameter(i + 1, args[i]);
        }

        return query.getResultList();
    }

    /**
     * @param domainClass the type of the returned list
     * @param filterConstraints
     * @return a list of domainObject
     */
    public List<T> list(final Class<T> domainClass, final Set<FilterConstraint> filterConstraints) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(domainClass);

        // select from
        final Root<T> from = criteriaQuery.from(domainClass);
        criteriaQuery.select(from);

        // filterConstraints
        filterQuery(criteriaBuilder, criteriaQuery, from, filterConstraints);

        // create query from criteria query
        final TypedQuery<T> query = entityManager.createQuery(criteriaQuery);

        return query.getResultList();
    }

    /**
     * @param domainClass the type of the returned domainObject
     * @param filterConstraints
     * @param sortConstraints
     * @return a list of domainObject
     */
    public List<T> list(final Class<T> domainClass, final Set<FilterConstraint> filterConstraints,
        final Set<SortConstraint> sortConstraints) {

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(domainClass);

        // select from
        final Root<T> from = criteriaQuery.from(domainClass);
        criteriaQuery.select(from);

        // filterConstraints
        filterQuery(criteriaBuilder, criteriaQuery, from, filterConstraints);

        // sortConstraints
        sortQuery(criteriaBuilder, criteriaQuery, from, sortConstraints);

        // create query from criteria query
        final TypedQuery<T> query = entityManager.createQuery(criteriaQuery);

        return query.getResultList();
    }

    /**
     * @param domainClass the type of the returned domainObject
     * @param start offset start
     * @param end offset end
     * @return a list of domainObject
     */
    public List<T> list(final Class<T> domainClass, final int start, final int end) {
        final Set<FilterConstraint> filterConstraints = Collections.emptySet();
        final Set<SortConstraint> sortConstraints = Collections.emptySet();
        return list(domainClass, start, end, filterConstraints, sortConstraints);
    }

    /**
     * @param domainClass the type of the returned domainObject
     * @param start offset start
     * @param end offset end
     * @param filterConstraints
     * @param sortConstraints
     * @return a list of domainObject
     */
    public List<T> list(final Class<T> domainClass, final int start, final int end,
        final Set<FilterConstraint> filterConstraints, final Set<SortConstraint> sortConstraints) {

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(domainClass);

        // select from
        final Root<T> from = criteriaQuery.from(domainClass);
        criteriaQuery.select(from);

        // filterConstraints
        filterQuery(criteriaBuilder, criteriaQuery, from, filterConstraints);

        // sortConstraints
        sortQuery(criteriaBuilder, criteriaQuery, from, sortConstraints);

        // create query from criteria query
        final TypedQuery<T> query = entityManager.createQuery(criteriaQuery);

        // pagination
        query.setFirstResult(start);
        query.setMaxResults(end - start);

        return query.getResultList();
    }

    /**
     * @param domainClass the type of the returned domainObject
     * @return the total number of domainObject
     */
    public Long count(final Class<T> domainClass) {
        return entityManager.createQuery(
            "select count(p) from " + domainClass.getSimpleName() + " p", Long.class)
            .getSingleResult();
    }

    /**
     * @param domainClass the type of the returned domainObject
     * @param filterConstraints
     * @return the number of domainObject applying to filterConstraints
     */
    public Long count(final Class<T> domainClass, final Set<FilterConstraint> filterConstraints) {

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);

        // select count from
        final Root<T> from = criteriaQuery.from(domainClass);
        final Expression<Long> count = criteriaBuilder.count(from);
        criteriaQuery.select(count);

        // filterConstraints
        filterQuery(criteriaBuilder, criteriaQuery, from, filterConstraints);

        // create query from criteria query
        final TypedQuery<Long> query = entityManager.createQuery(criteriaQuery);
        return query.getSingleResult();
    }

    void sortQuery(final CriteriaBuilder criteriaBuilder, final CriteriaQuery<?> criteriaQuery,
        final Root<T> from, final Set<SortConstraint> sortConstraints) {

        if (!sortConstraints.isEmpty()) {
            final LinkedList<Order> orders = new LinkedList<Order>();
            for (final SortConstraint sortConstraint : sortConstraints) {

                if (sortConstraint.isAscending()) {
                    orders.add(criteriaBuilder.asc(from.get(sortConstraint.getColumn())));
                } else {
                    orders.add(criteriaBuilder.desc(from.get(sortConstraint.getColumn())));
                }
            }
            criteriaQuery.orderBy(orders);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    void filterQuery(final CriteriaBuilder criteriaBuilder, final CriteriaQuery<?> criteriaQuery,
        final Root<T> from, final Set<FilterConstraint> filterConstraints) {

        if (!filterConstraints.isEmpty()) {
            final Predicate[] predicates = new Predicate[filterConstraints.size()];
            int i = 0;
            for (final FilterConstraint filterConstraint : filterConstraints) {
                final Predicate condition;

                final Class<? extends Comparable> javaType = filterConstraint.getJavaType();
                final Comparable typedValue = filterConstraint.getTypedValue();
                final Path<Object> expression = from.get(filterConstraint.getColumn());

                // eq
                if (filterConstraint.getOp() == FilterConstraintOpEnum.EQ) {
                    condition = criteriaBuilder.equal(expression, typedValue);

                    // ne
                } else if (filterConstraint.getOp() == FilterConstraintOpEnum.NE) {
                    condition = criteriaBuilder.notEqual(expression.as(javaType), typedValue);

                    // ge
                } else if (filterConstraint.getOp() == FilterConstraintOpEnum.GE) {
                    condition = criteriaBuilder.greaterThanOrEqualTo(expression.as(javaType),
                        typedValue);

                    // gt
                } else if (filterConstraint.getOp() == FilterConstraintOpEnum.GT) {
                    condition = criteriaBuilder.greaterThan(expression.as(javaType), typedValue);

                    // le
                } else if (filterConstraint.getOp() == FilterConstraintOpEnum.LE) {
                    condition = criteriaBuilder.lessThanOrEqualTo(expression.as(javaType),
                        typedValue);

                    // lt
                } else if (filterConstraint.getOp() == FilterConstraintOpEnum.LT) {
                    condition = criteriaBuilder.lessThan(expression.as(javaType), typedValue);

                    // sw
                } else if (filterConstraint.getOp() == FilterConstraintOpEnum.SW) {
                    condition = criteriaBuilder.like(expression.as(String.class), typedValue + "%");

                    // ew
                } else if (filterConstraint.getOp() == FilterConstraintOpEnum.EW) {
                    condition = criteriaBuilder.like(expression.as(String.class), "%" + typedValue);

                } else {
                    final UnsupportedOperationException e = new UnsupportedOperationException(
                        "unknown FilterConstraintOpEnum");
                    throw e;
                }

                predicates[i++] = condition;
            }
            criteriaQuery.where(predicates);
        }
    }
}
