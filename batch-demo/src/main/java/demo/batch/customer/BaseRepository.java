package demo.batch.customer;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.PathMetadataFactory;
import com.mysema.query.types.path.EntityPathBase;
import com.mysema.query.types.path.StringPath;

/**
 * BaseRepository
 */
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public abstract class BaseRepository<E, ID extends Serializable> {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @PersistenceContext(unitName = "dataStore")
    EntityManager entityManager;

    /**
     * @return E.class
     */
    protected abstract Class<E> getPersistentClass();

    /**
     * @return E EntityPathBase
     */
    protected abstract EntityPathBase<E> getEntityPathBase();

    /**
     * @param entity the entity
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void persist(final E entity) {
        entityManager.persist(entity);
    }

    /**
     * @param entity the entity
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void remove(final E entity) {
        entityManager.remove(entity);
    }

    /**
     * @param id the id
     * @return E
     */
    public E find(final ID id) {
        return entityManager.find(getPersistentClass(), id);
    }

    /**
     * @return a list of E
     */
    public List<E> list() {
        return new JPAQuery(entityManager).from(getEntityPathBase()).list(getEntityPathBase());
    }

    /**
     * @return the total number of E
     */
    public long count() {
        return new JPAQuery(entityManager).from(getEntityPathBase()).count();
    }

    public List<E> randomList(final int count) {
        // RAND() is h2 specific
        final PathMetadata<String> property = PathMetadataFactory.forVariable("RAND()");
        final StringPath target = new StringPath(property);
        final OrderSpecifier<String> orderSpecifier = new OrderSpecifier<String>(Order.ASC, target);

        return new JPAQuery(entityManager).from(getEntityPathBase()).orderBy(orderSpecifier)
            .limit(count).list(getEntityPathBase());
    }

    public E random() {
        final List<E> randomList = randomList(1);
        if (randomList.isEmpty()) return null;

        return randomList.get(0);
    }
}
