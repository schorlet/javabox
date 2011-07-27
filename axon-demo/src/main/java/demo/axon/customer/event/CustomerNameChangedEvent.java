package demo.axon.customer.event;

import org.axonframework.domain.DomainEvent;

public class CustomerNameChangedEvent extends DomainEvent {
    private static final long serialVersionUID = 1L;
    
    private final String newName;

    public CustomerNameChangedEvent(String newName) {
        this.newName = newName;
    }

    public String getNewName() {
        return newName;
    }
}
