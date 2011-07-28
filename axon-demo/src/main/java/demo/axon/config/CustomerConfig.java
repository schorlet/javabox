package demo.axon.config;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.annotation.AnnotationCommandHandlerAdapter;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerAdapter;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.util.AnnotatedHandlerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import demo.axon.customer.command.Customer;
import demo.axon.customer.command.CustomerCommandHandler;
import demo.axon.customer.command.CustomerValidation;
import demo.axon.customer.event.CustomerEventHandler;
import demo.axon.customer.event.CustomerEventRepository;
import demo.axon.customer.query.CustomerRepository;
import demo.axon.customer.query.CustomerValidationService;

@Configuration
public class CustomerConfig {

    @Autowired
    CommonConfig common;
    @Autowired
    DataConfig data;

    @Bean
    public CustomerRepository customerRepository() {
        return new CustomerRepository();
    }

    @Bean
    @DependsOn({ "customerRepository" })
    public CustomerValidation customerValidation() {
        return new CustomerValidationService(customerRepository());
    }

    @Bean
    @DependsOn({ "eventBus", "snapshotEventStore" })
    public EventSourcingRepository<Customer> customerEventRepository() {
        final CustomerEventRepository repository = new CustomerEventRepository(Customer.class);
        repository.setEventBus(common.eventBus());
        repository.setEventStore(data.snapshotEventStore());
        return repository;
    }

    /*
     * CommandHandler
     */

    @Bean
    @DependsOn({ "customerEventRepository", "customerValidation" })
    CustomerCommandHandler customerCommandHandler() {
        final CustomerCommandHandler commandHandler = new CustomerCommandHandler(
            customerEventRepository(), customerValidation());
        return commandHandler;
    }

    @Bean
    @DependsOn({ "customerCommandHandler", "commandBus" })
    public CommandHandler<Object> customerCommandHandlerAdapter() {
        // customerCommandHandler is automatically subscribed to all command objects
        // matching with @CommandHandler annotated methods
        return new AnnotationCommandHandlerAdapter(customerCommandHandler(), common.commandBus());
    }

    /*
     * EventHandler
     */

    @Bean
    CustomerEventHandler customerEventHandler() {
        return new CustomerEventHandler();
    }

    @Bean
    @DependsOn({ "customerEventHandler", "eventBus" })
    public AnnotatedHandlerAdapter customerEventHandlerAdapter() {
        // customerEventHandler is automatically subscribed to eventBus
        // via AnnotationEventListenerAdapter @PostConstruct
        return new AnnotationEventListenerAdapter(customerEventHandler(), common.eventBus());
    }

}
