package demo.axon.customer.command;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerCommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomerCommandHandler.class);

    private final Repository<Customer> repository;
    private final CustomerValidation validation;

    public CustomerCommandHandler(final Repository<Customer> repository,
        final CustomerValidation validation) {
        this.repository = repository;
        this.validation = validation;
    }

    @CommandHandler
    public void handle(final CreateCustomerCommand command) {
        logger.debug("[Handle] {}", string(command));

        Validate.notNull(command.getIdentifier(), "Customer identifier may not be null");
        Validate.notNull(command.getName(), "Name may not be null");
        validation.uniqueCustomerName(command.getName());

        final Customer customer = new Customer(command.getIdentifier(), command.getName());
        repository.add(customer);
    }

    @CommandHandler
    public void handle(final ChangeCustomerNameCommand command) {
        logger.debug("[Handle] {}", string(command));

        Validate.notNull(command.getCustomerId(), "Customer identifier may not be null");
        Validate.notNull(command.getCustomerNewName(), "Customer name may not be null");
        validation.uniqueCustomerName(command.getCustomerNewName());

        final Customer customer = repository.load(command.getCustomerId(), null);
        customer.changeName(command.getCustomerNewName());
        repository.add(customer);
    }

    @CommandHandler
    public void handle(final RemoveCustomerCommand command) {
        logger.debug("[Handle] {}", string(command));

        Validate.notNull(command.getCustomerId(), "Customer identifier may not be null");

        final Customer customer = repository.load(command.getCustomerId(), null);
        customer.remove();
        repository.add(customer);
    }

    String string(final Object object) {
        return ToStringBuilder.reflectionToString(object, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
