package demo.axon.customer.command;

import java.util.UUID;

import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;

import demo.axon.customer.event.CreatedCustomerEvent;
import demo.axon.customer.event.CustomerNameChangedEvent;
import demo.axon.customer.event.RemovedCustomerEvent;

public class Customer extends AbstractAnnotatedAggregateRoot {
    @SuppressWarnings("unused")
    private String name;
    
    /*
     * NOTE:
     * There is currently a limitation in Axon that requires all events
     * to be applied to the aggregate root.
     *
     * https://groups.google.com/group/axonframework/browse_thread/thread/5f0c11b28d1c7b0c#
     */

    Customer(UUID id) {
        super(id);
    }

    Customer(UUID id, String name) {
        super(id);
        apply(new CreatedCustomerEvent(name));
    }

    /*
     * RULE:
     * Validation shoud occur exclusively in the "regular" methods called on
     * the aggregate (i.e not the @EventHandler annotated ones).
     */

    public void changeName(String newName) {
        apply(new CustomerNameChangedEvent(newName));
    }

    public void remove() {
        apply(new RemovedCustomerEvent());
    }

    /*
     * SAME RULE:
     * eg: No logic in the @EventHandler except strictly changing the state as
     * described by the event.
     */
    
    @EventHandler
    protected void handleCreatedCustomerEvent(CreatedCustomerEvent event) {
        name = event.getName();
    }

    @EventHandler
    protected void handleCustomerNameChangedEvent(CustomerNameChangedEvent event) {
        name = event.getNewName();
    }

}
