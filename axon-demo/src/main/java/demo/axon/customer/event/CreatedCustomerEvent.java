package demo.axon.customer.event;

import org.axonframework.domain.DomainEvent;

public class CreatedCustomerEvent extends DomainEvent {
    private static final long serialVersionUID = 1L;
    
    private String name;

    public CreatedCustomerEvent(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}
