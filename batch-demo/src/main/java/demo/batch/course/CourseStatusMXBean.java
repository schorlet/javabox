package demo.batch.course;

/**
 * CourseStatusMXBean
 */
public interface CourseStatusMXBean {

    /**
     * the number of performed tasks.
     * @return the number of performed tasks
     */
    int getProcessed();

    /**
     * the number of remaining tasks.
     * @return the number of remaining tasks
     */
    int getRemaining();

    /**
     * the number of running threads.
     * @return the number of running threads
     */
    int getThreadPoolSize();
}
