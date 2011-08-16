package demo.batch.customer.course;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import demo.batch.course.Course;
import demo.batch.customer.CustomerRepository;
import demo.batch.customer.Repositories;

/**
 * UpdateCustomerCourse
 */
@Component
public class UpdateCustomerCourse extends Course {
    @Autowired
    Repositories repositories;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Override
    protected int getThreadPoolSize() {
        return 3;
    }

    private final int lotSize = 50;

    /**
     * @return the lotSize
     */
    public int getLotSize() {
        return lotSize;
    }

    private Queue<List<String>> workingQueue;

    /**
     * @return the workingQueue
     */
    public Queue<List<String>> getWorkingQueue() {
        return workingQueue;
    }

    @Override
    protected void performBefore() {
        super.performBefore();

        logger.info("lotSize: {}", lotSize);

        workingQueue = new ConcurrentLinkedQueue<List<String>>();
        prepareWorkingQueue(workingQueue, repositories.getCustomerRepository());

        logger.info("workingQueueSize: {}", workingQueue.size());

        registerMBean(new UpdateCustomerCourseStatus(this));
    }

    @Override
    protected void performAfter() {
        unregisterMBean();

        workingQueue.clear();
        workingQueue = null;

        super.performAfter();
    }

    void prepareWorkingQueue(final Queue<List<String>> workingQueue,
        final CustomerRepository customerRepository) {
        final List<String> customerIds = customerRepository.listIdentifier();

        final int customerSize = customerIds.size();
        logger.info("customers count: {}", customerIds.size());

        for (int i = 0; i < customerSize; i += lotSize) {
            int toIndex = i + lotSize;
            toIndex = toIndex < customerSize ? toIndex : customerSize;

            final List<String> subList = customerIds.subList(i, toIndex);
            workingQueue.add(subList);
        }
    }

    @Override
    protected Runnable createProcess(final CountDownLatch countDownLatch) {
        return new UpdateCustomerProcess(workingQueue, repositories, transactionManager,
            countDownLatch);
    }

}
