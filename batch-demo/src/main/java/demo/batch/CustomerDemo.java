package demo.batch;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import demo.batch.config.AppConfig;
import demo.batch.customer.Repositories;
import demo.batch.customer.course.InitCustomerCourse;
import demo.batch.customer.course.UpdateCustomerCourse;
import demo.batch.customer.entity.CustomerEntity;
import demo.batch.customer.entity.OrderEntity;
import demo.batch.customer.entity.OrderItemEntity;

/**
 * CustomerDemo
 */
public class CustomerDemo {
    private static final Logger logger = LoggerFactory.getLogger(CustomerDemo.class);

    public static void main(final String[] args) {
        final AbstractApplicationContext context = getApplicationContext();
        try {
            demo(context);
        } catch (final Exception e) {
            logger.error("demo", e);
        }
        context.close();
    }

    static AbstractApplicationContext getApplicationContext() {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        context.register(AppConfig.class);
        context.scan("demo.batch.customer");

        context.refresh();
        return context;
    }

    public static void demo(final ApplicationContext context) throws InterruptedException {
        final InitCustomerCourse initCourse = context.getBean(InitCustomerCourse.class);
        final UpdateCustomerCourse updateCourse = context.getBean(UpdateCustomerCourse.class);

        initCourse.execute();
        updateCourse.execute();

        final Repositories repositories = context.getBean(Repositories.class);
        printAll(repositories);

        long count = repositories.getCustomerRepository().count();
        logger.info("customers count: {}", count);

        count = repositories.getOrderRepository().count();
        logger.info("orders count: {}", count);

        count = repositories.getProductRepository().count();
        logger.info("products count: {}", count);
    }

    static void printAll(final Repositories repositories) {
        final List<CustomerEntity> customers = repositories.getCustomerRepository().listAll();
        final String format = "%20s | %23s | %2s | %20s%n";
        int rowCount = 0;

        for (final CustomerEntity customer : customers) {

            final Set<OrderEntity> orders = customer.getOrders();

            if (orders.isEmpty()) {
                System.out.printf(format, customer.getName(), "", "", "");
                rowCount++;

            } else {
                for (final OrderEntity order : orders) {

                    final Set<OrderItemEntity> items = order.getItems();

                    if (items.isEmpty()) {
                        System.out.printf(format, customer.getName(), order.getDate(), "", "");
                        rowCount++;

                    } else {
                        for (final OrderItemEntity item : items) {

                            System.out.printf(format, customer.getName(), order.getDate(),
                                item.getQuantity(), item.getProduct().getname());
                            rowCount++;
                        }
                    }
                }
            }
        }
        System.out.printf("%s rows%n", rowCount);
    }

}
