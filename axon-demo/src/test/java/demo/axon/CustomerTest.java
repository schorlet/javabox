package demo.axon;

import java.util.HashSet;

import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.axon.customer.command.ChangeCustomerNameCommand;
import demo.axon.customer.command.CreateCustomerCommand;
import demo.axon.customer.command.Customer;
import demo.axon.customer.command.CustomerCommandHandler;
import demo.axon.customer.command.CustomerValidation;
import demo.axon.customer.command.RemoveCustomerCommand;
import demo.axon.customer.event.CreatedCustomerEvent;
import demo.axon.customer.event.CustomerNameChangedEvent;
import demo.axon.customer.event.RemovedCustomerEvent;

public class CustomerTest {
    static final Logger logger = LoggerFactory.getLogger(CustomerTest.class);

    FixtureConfiguration fixture;
    CustomerValidationTest customerValidation;
    
    @Before
    public void setUp() {
        fixture = Fixtures.newGivenWhenThenFixture();

        EventSourcingRepository<Customer> repository = fixture
            .createGenericRepository(Customer.class);

        customerValidation = new CustomerValidationTest();

        CustomerCommandHandler commandHandler = new CustomerCommandHandler(repository,
            customerValidation);
        
        fixture.registerAnnotatedCommandHandler(commandHandler);
    }

    @Test
    public void testCreate() {
        fixture.given().when(new CreateCustomerCommand("1")).expectVoidReturnType()
            .expectEvents(new CreatedCustomerEvent("1"));
    }

    @Test
    public void testCreateException() {
        customerValidation.addName("1");
        
        fixture.given(new CreatedCustomerEvent("1")).when(new CreateCustomerCommand("1"))
            .expectException(IllegalArgumentException.class);
    }

    @Test
    public void testRename() {
        fixture.given(new CreatedCustomerEvent("1")).when(
            new ChangeCustomerNameCommand(fixture.getAggregateIdentifier(), "2"))
            .expectVoidReturnType().expectEvents(new CustomerNameChangedEvent("2"));
    }

    @Test
    public void testDelete() {
        fixture.given(new CreatedCustomerEvent("1")).when(
            new RemoveCustomerCommand(fixture.getAggregateIdentifier()))
            .expectVoidReturnType().expectEvents(new RemovedCustomerEvent());
    }

    /**
     * CustomerValidationTest.
     */
    class CustomerValidationTest implements CustomerValidation {
        HashSet<String> names = new HashSet<String>();

        void addName(String name) {
            names.add(name);
        }
        
        @Override
        public void uniqueCustomerName(String name) {
            logger.debug("[Validation] uniqueCustomerName {}", name);

            boolean added = names.add(name);
            if (!added) {
                throw new IllegalArgumentException(String.format(
                    "Customer name (%s) may be unique", name));
            }
        }
    }
}
