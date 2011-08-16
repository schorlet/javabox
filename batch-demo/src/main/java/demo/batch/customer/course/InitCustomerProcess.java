package demo.batch.customer.course;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.transaction.PlatformTransactionManager;

import demo.batch.course.Process;
import demo.batch.customer.Repositories;
import demo.batch.customer.course.InitCustomerCourse.Counter;
import demo.batch.customer.entity.CustomerEntity;
import demo.batch.customer.entity.OrderEntity;
import demo.batch.customer.entity.OrderItemEntity;
import demo.batch.customer.entity.ProductEntity;

/**
 * InitCustomerProcess
 */
class InitCustomerProcess extends Process {
    final Repositories repositories;

    final Counter counter;

    public InitCustomerProcess(final Counter counter, final Repositories repositories,
        final PlatformTransactionManager transactionManager, final CountDownLatch countDownLatch) {

        super(countDownLatch, transactionManager);

        this.counter = counter;
        this.repositories = repositories;
    }

    @Override
    protected void perform() {
        while (counter.decrement() > 0) {
            logger.info("[process {}] decrement {}", getProcessId(), counter.getValue());

            // start time
            final long start = System.currentTimeMillis();

            // create new customer
            createCustomer();

            // create new product
            createProduct();

            // create new order
            createOrder();

            // log elapsed time
            chrono.debug("[process {}] {}ms", getProcessId(), System.currentTimeMillis() - start);
        }
    }

    /**
     * createCustomer
     */
    void createCustomer() {
        final UUID randomUUID = UUID.randomUUID();
        logger.debug("[process {}] create new customer {}", getProcessId(), randomUUID);

        repositories.getCustomerRepository().create(randomUUID,
            RandomStringUtils.randomAlphanumeric(20));
    }

    /**
     * createProduct
     */
    void createProduct() {
        final UUID randomUUID = UUID.randomUUID();
        logger.debug("[process {}] create new product {}", getProcessId(), randomUUID);

        repositories.getProductRepository().create(randomUUID,
            RandomStringUtils.randomAlphanumeric(20));
    }

    /**
     * @param customer
     * @param product
     */
    void createOrder() {
        final CustomerEntity customer = repositories.getCustomerRepository().random();
        final List<ProductEntity> products = repositories.getProductRepository().randomList(
            1 + RandomUtils.nextInt(4));

        // order items
        final Set<OrderItemEntity> items = new HashSet<OrderItemEntity>(products.size());
        for (final ProductEntity product : products) {
            // order item
            final OrderItemEntity item = new OrderItemEntity();
            item.setProduct(product);
            item.setQuantity(1 + RandomUtils.nextInt(10));
            items.add(item);
        }

        // order
        final OrderEntity order = new OrderEntity();
        order.setIdentifier(UUID.randomUUID());
        order.setCustomer(customer);
        order.setItems(items);
        order.setDate(randomDate());

        logger.debug("[process {}] create new order {}", order);

        // persist order
        repositories.getOrderRepository().persist(order);
    }

    /**
     * @return a date between 0 and System.currentTimeMillis(), 
     * eg. between 01/01/1970 and now 
     */
    Date randomDate() {
        final double date = Math.random() * System.currentTimeMillis();
        return new Date((long) date);
    }
}
