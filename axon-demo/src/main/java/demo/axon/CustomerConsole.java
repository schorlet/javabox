package demo.axon;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import org.axonframework.commandhandling.CommandBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import demo.axon.config.AppConfig;
import demo.axon.customer.command.ChangeCustomerNameCommand;
import demo.axon.customer.command.CreateCustomerCommand;
import demo.axon.customer.command.RemoveCustomerCommand;
import demo.axon.customer.query.CustomerEntity;
import demo.axon.customer.query.CustomerRepository;

public class CustomerConsole {
    private static final Logger logger = LoggerFactory.getLogger(CustomerConsole.class);

    @Autowired
    private CommandBus commandBus;

    @Autowired
    private CustomerRepository customerRepository;

    public static void main(final String[] args) {
        final CustomerConsole launcher = new CustomerConsole();
        launcher.initializeContext();
        launcher.processCommands(System.in);
        launcher.shutDown();
    }

    private void processCommands(final InputStream in) {
        final Scanner scanner = new Scanner(in);
        String command = null;

        while (!"quit".equals(command)) {
            System.out.print("> ");
            command = scanner.nextLine();

            if (command.startsWith("add ")) {
                final String name = command.substring(4);

                final CreateCustomerCommand create = new CreateCustomerCommand(name);
                commandBus.dispatch(create, LoggerCallback.INSTANCE);

            } else if (command.startsWith("delete ")) {
                final String identifier = command.substring(7);
                final UUID uuid = UUID.fromString(identifier);

                final RemoveCustomerCommand remove = new RemoveCustomerCommand(uuid);
                commandBus.dispatch(remove, LoggerCallback.INSTANCE);

            } else if (command.startsWith("rename ")) {
                final int indexOfNewName = command.indexOf(' ', 7) + 1;
                final String identifier = command.substring(7, indexOfNewName - 1);
                final String newName = command.substring(indexOfNewName);
                final UUID uuid = UUID.fromString(identifier);

                final ChangeCustomerNameCommand rename = new ChangeCustomerNameCommand(uuid,
                    newName);
                commandBus.dispatch(rename, LoggerCallback.INSTANCE);

            } else if (command.startsWith("list")) {
                final List<CustomerEntity> customers = customerRepository.findAll();
                logger.debug("customer list size: {}", customers.size());

                for (final CustomerEntity customer : customers) {
                    logger.debug("  {}", customer);
                }

            } else if (command.startsWith("quit")) {
                // will automatically quit
            } else {
                System.out.println("Supported commands: ");
                System.out.println("- 'add <name>'");
                System.out.println("- 'delete <id>'");
                System.out.println("- 'rename <id> <new name>'");
                System.out.println("- 'list'");
                System.out.println("- 'quit'");
            }
        }
        scanner.close();
    }

    private void initializeContext() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AppConfig.class);
        context.scan("demo.axon.customer");
        context.refresh();

        final ConfigurableListableBeanFactory factory = (ConfigurableListableBeanFactory) context
            .getAutowireCapableBeanFactory();
        factory.autowireBean(this);
    }

    private void shutDown() {
        System.exit(0);
    }

}
