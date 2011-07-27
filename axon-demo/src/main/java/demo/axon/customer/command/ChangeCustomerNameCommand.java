package demo.axon.customer.command;

import java.util.UUID;

public class ChangeCustomerNameCommand {
    private final UUID customerId;
    private final String customerNewName;

    public ChangeCustomerNameCommand(UUID customerId, String customerNewName) {
        this.customerId = customerId;
        this.customerNewName = customerNewName;
    }
    
    public UUID getCustomerId() {
        return customerId;
    }

    public String getCustomerNewName() {
        return customerNewName;
    }

}
