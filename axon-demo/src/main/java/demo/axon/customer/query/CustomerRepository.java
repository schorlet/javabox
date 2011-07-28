package demo.axon.customer.query;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;

@Repository
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class CustomerRepository {
    static final QCustomerEntity customerEntity = QCustomerEntity.customerEntity;

    @PersistenceContext(unitName = "dataStore")
    EntityManager entityManager;

    public List<CustomerEntity> findAll() {
        final JPQLQuery query = new JPAQuery(entityManager);

        final List<CustomerEntity> customers = query.from(customerEntity).list(customerEntity);
        return customers;
    }

    public CustomerEntity find(final UUID identifier) {
        final JPQLQuery query = new JPAQuery(entityManager);

        final CustomerEntity customer = query.from(customerEntity)
            .where(customerEntity.identifier.eq(identifier.toString()))
            .uniqueResult(customerEntity);
        return customer;
    }

    public List<CustomerEntity> findByName(final String name) {
        final JPQLQuery query = new JPAQuery(entityManager);

        final List<CustomerEntity> customers = query.from(customerEntity)
            .where(customerEntity.name.eq(name)).list(customerEntity);
        return customers;
    }

    public List<CustomerEntity> findByExample(final CustomerEntity customer) {
        final JPQLQuery query = new JPAQuery(entityManager);

        final List<CustomerEntity> customers = query.from(customerEntity)
            .where(customerEntity.eq(customer)).list(customerEntity);
        return customers;
    }
}
