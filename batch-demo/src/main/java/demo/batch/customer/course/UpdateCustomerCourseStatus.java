package demo.batch.customer.course;

import demo.batch.course.CourseStatusMXBean;

/**
 * CourseStatus
 *
 * @author sch
 * @version $Revison $Date
 * @since 9 août 2011
 */
public class UpdateCustomerCourseStatus implements CourseStatusMXBean {

    final UpdateCustomerCourse course;

    final int total;

    final int threadPoolSize;

    /**
     * @param course
     */
    public UpdateCustomerCourseStatus(final UpdateCustomerCourse course) {
        this.threadPoolSize = course.getThreadPoolSize();
        this.course = course;
        total = course.getLotSize() * course.getWorkingQueue().size();
    }

    @Override
    public int getProcessed() {
        return total - course.getLotSize() * course.getWorkingQueue().size();
    }

    @Override
    public int getRemaining() {
        return course.getLotSize() * course.getWorkingQueue().size();
    }

    @Override
    public int getThreadPoolSize() {
        return threadPoolSize;
    }
}
