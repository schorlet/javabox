package demo.axon.customer.command;

import java.util.UUID;

public class RemoveCustomerCommand {
    private final UUID customerId;

    public RemoveCustomerCommand(UUID customerId) {
        this.customerId = customerId;
    }
    
    public UUID getCustomerId() {
        return customerId;
    }

}
