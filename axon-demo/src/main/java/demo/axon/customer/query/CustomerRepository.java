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
        JPQLQuery query = new JPAQuery(entityManager);

        List<CustomerEntity> customers = query.from(customerEntity).list(customerEntity);
        return customers;
    }

    public CustomerEntity find(UUID identifier) {
        JPQLQuery query = new JPAQuery(entityManager);

        CustomerEntity customer = query.from(customerEntity).where(
            customerEntity.identifier.eq(identifier.toString())).uniqueResult(
            customerEntity);
        return customer;
    }

    public List<CustomerEntity> findByName(String name) {
        JPQLQuery query = new JPAQuery(entityManager);

        List<CustomerEntity> customers = query.from(customerEntity).where(
            customerEntity.name.eq(name)).list(customerEntity);
        return customers;
    }

    public List<CustomerEntity> findByExample(CustomerEntity customer) {
        JPQLQuery query = new JPAQuery(entityManager);

        List<CustomerEntity> customers = query.from(customerEntity).where(
            customerEntity.eq(customer)).list(customerEntity);
        return customers;
    }
}
