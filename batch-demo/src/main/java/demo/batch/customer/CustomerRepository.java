package demo.batch.customer;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.math.RandomUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.jpa.impl.JPADeleteClause;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.jpa.impl.JPAUpdateClause;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.path.EntityPathBase;

import demo.batch.customer.entity.CustomerEntity;
import demo.batch.customer.entity.QCustomerEntity;
import demo.batch.customer.entity.QOrderEntity;

@Repository
public class CustomerRepository extends BaseRepository<CustomerEntity, String> {

    static final QCustomerEntity qCustomerEntity = QCustomerEntity.customerEntity;

    @Override
    protected Class<CustomerEntity> getPersistentClass() {
        return CustomerEntity.class;
    }

    @Override
    protected EntityPathBase<CustomerEntity> getEntityPathBase() {
        return qCustomerEntity;
    }

    /**
     * @return a list of customer identifiers
     */
    public List<String> listIdentifier() {
        return new JPAQuery(entityManager).from(qCustomerEntity).list(qCustomerEntity.identifier);
    }

    /**
     * @param identifier the identifier
     * @param name the name
     * @return CustomerEntity
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public void create(final UUID identifier, final String name) {
        final CustomerEntity entity = new CustomerEntity();
        entity.setIdentifier(identifier);
        entity.setName(name);
        super.persist(entity);
    }

    /**
     * @param identifier the identifier
     * @return the amount of affected rows
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public long delete(final UUID identifier) {
        final JPADeleteClause deleteClause = new JPADeleteClause(entityManager, qCustomerEntity)
            .where(qCustomerEntity.identifier.eq(identifier.toString()));

        final long execute = deleteClause.execute();
        logger.trace("delete {}", execute);
        return execute;
    }

    /**
     * from CustomerEntity customerEntity
     * left join fetch customerEntity.orders as orderEntity
     * order by customerEntity.name asc, orderEntity.date asc
     * 
     * @return all customers with orders and products
     */
    public List<CustomerEntity> listAll() {
        final QOrderEntity qOrderEntity = QOrderEntity.orderEntity;

        final JPAQuery query = new JPAQuery(entityManager).from(qCustomerEntity)
            .leftJoin(qCustomerEntity.orders, qOrderEntity).fetch()
            .orderBy(qCustomerEntity.name.asc(), qOrderEntity.date.asc());

        return query.listDistinct(qCustomerEntity);
    }

    /**
     * @param identifier the identifier
     * @param newName the new name
     * @return the amount of affected rows
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public long updateName(final UUID identifier, final String newName) {
        final JPAUpdateClause updateClause = new JPAUpdateClause(entityManager, qCustomerEntity)
            .set(qCustomerEntity.name, newName).where(
                qCustomerEntity.identifier.eq(identifier.toString()));

        final long execute = updateClause.execute();
        if (execute == 0) {
            logger.warn("updateName {} {}", execute, identifier);
        } else {
            logger.trace("updateName {} {}", execute, identifier);
        }
        return execute;
    }

    /**
     * @param identifier the identifier
     * @param newName the new name
     * @return the amount of affected rows
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public long updateNameSlow(final UUID identifier, final String newName) {

        final String identifierString = identifier.toString();

        final int subSize = 6;
        final int identifierStringLength = identifierString.length();
        final int identifierPartLength = identifierStringLength / subSize;

        /*
         * create an array as: 
         * subLength[0] = random length
         * subLength[1] = random length
         * subLength[subSize] = random length
         */
        final int subLength[] = new int[subSize];
        for (int j = 0; j < subSize; j++) {
            subLength[j] = RandomUtils.nextInt(identifierPartLength);
        }

        /*
         * take care about the two substring usages:  
         * qCustomerEntity.identifier.substring(beginIndex, length)
         *           identifierString.substring(beginIndex, endIndex)
         */
        int i = 0;

        BooleanExpression expression = qCustomerEntity.identifier.substring(0, subLength[i]).eq(
            identifierString.substring(0, subLength[i]));

        for (; i < subLength.length - 1; i++) {
            expression = expression.and(qCustomerEntity.identifier.substring(subLength[i],
                subLength[i + 1]).eq(
                identifierString.substring(subLength[i], subLength[i] + subLength[i + 1])));
        }

        expression = expression.and(qCustomerEntity.identifier.substring(subLength[i],
            identifierStringLength - subLength[i]).eq(
            identifierString.substring(subLength[i], identifierStringLength)));

        logger.trace("updateNameSlow {}", expression);

        // create updateClause
        final JPAUpdateClause updateClause = new JPAUpdateClause(entityManager, qCustomerEntity)
            .set(qCustomerEntity.name, newName).where(expression);

        // execute updateClause
        final long execute = updateClause.execute();
        if (execute == 0) {
            logger.warn("updateNameSlow {}", execute);
        } else {
            logger.trace("updateNameSlow {} {}", execute, identifier);
        }

        return execute;
    }

}
