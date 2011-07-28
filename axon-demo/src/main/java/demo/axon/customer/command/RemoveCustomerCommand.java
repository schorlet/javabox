package demo.axon.customer.command;

import java.util.UUID;

public class RemoveCustomerCommand {
    private final UUID customerId;

    public RemoveCustomerCommand(final UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

}
