package demo.axon.customer.event;

import org.axonframework.domain.DomainEvent;

public class CreatedCustomerEvent extends DomainEvent {
    private static final long serialVersionUID = 1L;

    private final String name;

    public CreatedCustomerEvent(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
