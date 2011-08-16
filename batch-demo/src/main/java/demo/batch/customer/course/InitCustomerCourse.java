package demo.batch.customer.course;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import demo.batch.course.Course;
import demo.batch.customer.Repositories;

/**
 * InitCustomerCourse
 */
@Component
public class InitCustomerCourse extends Course {
    @Autowired
    Repositories repositories;

    @Autowired
    PlatformTransactionManager transactionManager;

    final Counter counter;

    public InitCustomerCourse() {
        counter = new Counter(getThreadPoolSize() * 300);
    }

    @Override
    protected int getThreadPoolSize() {
        return 6;
    }

    @Override
    protected void performBefore() {
        super.performBefore();

        final InitCustomerCourseStatus courseStatusMXBean = new InitCustomerCourseStatus(
            getThreadPoolSize(), counter);
        registerMBean(courseStatusMXBean);
    }

    @Override
    protected void performAfter() {
        unregisterMBean();
        super.performAfter();
    }

    @Override
    protected Runnable createProcess(final CountDownLatch countDownLatch) {
        return new InitCustomerProcess(counter, repositories, transactionManager, countDownLatch);
    }

    /**
     * Counter
     */
    class Counter {
        final AtomicInteger count;

        Counter(final int i) {
            count = new AtomicInteger(i);
        }

        public int decrement() {
            return count.decrementAndGet();
        }

        public int getValue() {
            return count.get();
        }
    }
}
