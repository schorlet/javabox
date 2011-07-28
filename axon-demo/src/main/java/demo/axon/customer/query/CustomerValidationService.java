package demo.axon.customer.query;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.axon.customer.command.CustomerValidation;

public class CustomerValidationService implements CustomerValidation {
    private static final Logger logger = LoggerFactory.getLogger(CustomerValidationService.class);

    private final CustomerRepository customerRepository;

    public CustomerValidationService(final CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void uniqueCustomerName(final String name) {
        logger.debug("[Validation] uniqueCustomerName {}", name);

        final List<CustomerEntity> list = customerRepository.findByName(name);

        if (!list.isEmpty())
            throw new IllegalArgumentException(String.format("Customer name (%s) already exists",
                name));
    }
}
