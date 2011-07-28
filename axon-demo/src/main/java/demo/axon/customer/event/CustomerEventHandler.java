package demo.axon.customer.event;

import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.jpa.impl.JPADeleteClause;
import com.mysema.query.jpa.impl.JPAUpdateClause;

import demo.axon.customer.query.CustomerEntity;
import demo.axon.customer.query.QCustomerEntity;

@Repository
@Transactional
public class CustomerEventHandler {
    static final QCustomerEntity customerEntity = QCustomerEntity.customerEntity;

    @PersistenceContext(unitName = "dataStore")
    EntityManager entityManager;

    /*
     * RULE:
     * @EventHandler methods should only apply state changes based on the event.
     * They should never do any validation.
     */

    @EventHandler
    protected void handleCreatedCustomerEvent(final CreatedCustomerEvent event) {
        final CustomerEntity entry = new CustomerEntity();
        entry.setIdentifier(event.getAggregateIdentifier());
        entry.setName(event.getName());
        entityManager.persist(entry);
    }

    @EventHandler
    protected void handleCustomerNameChangedEvent(final CustomerNameChangedEvent event) {
        final UUID identifier = event.getAggregateIdentifier();
        final String newName = event.getNewName();

        new JPAUpdateClause(entityManager, customerEntity)
            .where(customerEntity.identifier.eq(identifier.toString()))
            .set(customerEntity.name, newName).execute();
    }

    @EventHandler
    protected void handleRemovedCustomerEvent(final RemovedCustomerEvent event) {
        final UUID identifier = event.getAggregateIdentifier();

        new JPADeleteClause(entityManager, customerEntity).where(
            customerEntity.identifier.eq(identifier.toString())).execute();
    }
}
