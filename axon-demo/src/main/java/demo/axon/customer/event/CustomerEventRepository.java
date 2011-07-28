package demo.axon.customer.event;

import org.axonframework.eventsourcing.GenericEventSourcingRepository;

import demo.axon.customer.command.Customer;

public class CustomerEventRepository extends GenericEventSourcingRepository<Customer> {

    public CustomerEventRepository(final Class<Customer> aggregateType) {
        super(aggregateType);
    }

}
