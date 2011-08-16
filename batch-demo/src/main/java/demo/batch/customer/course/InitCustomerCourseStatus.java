package demo.batch.customer.course;

import demo.batch.course.CourseStatusMXBean;
import demo.batch.customer.course.InitCustomerCourse.Counter;

/**
 * InitCustomerCourseStatus
 */
public class InitCustomerCourseStatus implements CourseStatusMXBean {

    final Counter counter;

    final int total;

    final int threadPoolSize;

    /**
     * @param threadPoolSize
     * @param counter
     */
    public InitCustomerCourseStatus(final int threadPoolSize, final Counter counter) {
        this.threadPoolSize = threadPoolSize;
        this.counter = counter;

        this.total = counter.getValue();
    }

    @Override
    public int getProcessed() {
        return total - counter.getValue();
    }

    @Override
    public int getRemaining() {
        return counter.getValue();
    }

    @Override
    public int getThreadPoolSize() {
        return threadPoolSize;
    }

}
