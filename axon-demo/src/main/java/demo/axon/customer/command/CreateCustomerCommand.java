package demo.axon.customer.command;

import java.util.UUID;

public class CreateCustomerCommand {
    private final UUID identifier;
    private final String name;

    public CreateCustomerCommand(final UUID identifier, final String name) {
        this.identifier = identifier;
        this.name = name;
    }

    public CreateCustomerCommand(final String name) {
        this.identifier = UUID.randomUUID();
        this.name = name;
    }

    public UUID getIdentifier() {
        return identifier;
    }

    public String getName() {
        return name;
    }
}
