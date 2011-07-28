package demo.axon;

import java.net.MalformedURLException;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.axonframework.commandhandling.CommandBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import demo.axon.config.AppConfig;
import demo.axon.customer.command.ChangeCustomerNameCommand;
import demo.axon.customer.command.CreateCustomerCommand;
import demo.axon.customer.command.RemoveCustomerCommand;
import demo.axon.customer.query.CustomerEntity;
import demo.axon.customer.query.CustomerRepository;

public class CustomerDemo {
    private static final Logger logger = LoggerFactory.getLogger(CustomerDemo.class);

    public static void main(final String[] args) throws MalformedURLException {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(AppConfig.class);
        context.scan("demo.axon.customer");
        context.refresh();

        // create customer
        final CommandBus commandBus = context.getBean(CommandBus.class);
        commandBus.dispatch(new CreateCustomerCommand("demo"), LoggerCallback.INSTANCE);

        // select all customers
        final CustomerRepository customerRepository = context.getBean(CustomerRepository.class);
        final List<CustomerEntity> customers = customerRepository.findAll();
        logger.debug("customer list size: {}", customers.size());

        for (final CustomerEntity customer : customers) {
            final CustomerEntity entity = customerRepository.find(customer.getIdentifier());
            logger.debug("customer: {}", entity);

            for (int i = 0; i < 4; i++) {
                // change customer name
                final String randomName = RandomStringUtils.randomAlphanumeric(8);

                commandBus.dispatch(new ChangeCustomerNameCommand(entity.getIdentifier(),
                    randomName), LoggerCallback.INSTANCE);

                // callback should show an exception
                commandBus.dispatch(new CreateCustomerCommand(randomName), LoggerCallback.INSTANCE);
            }

            // delete customer
            commandBus.dispatch(new RemoveCustomerCommand(entity.getIdentifier()),
                LoggerCallback.INSTANCE);
        }
    }
}
