package demo.batch.course;

import java.lang.management.ManagementFactory;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.ObjectName;

/**
 * Course
 *
 * @author sch
 * @version $Revison $Date
 * @since 30 juil. 2011
 */
public abstract class Course extends Perform {

    // deamon timer
    final Timer timer = new Timer(true);

    @Override
    protected void performBefore() {
        timer.schedule(new CourseMemory(), 1000, 5000);
    }

    @Override
    protected void performAfter() {
        timer.cancel();
    }

    @Override
    protected final void perform() throws InterruptedException {
        // the threadPoolSize
        final int threadPoolSize = getThreadPoolSize();
        logger.info("threadPoolSize: {}", threadPoolSize);

        // start time
        final long start = System.currentTimeMillis();

        // the threadPool
        final ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);

        // the countDownLatch
        final CountDownLatch countDownLatch = new CountDownLatch(threadPoolSize);

        for (int i = 0; i < threadPoolSize; i++) {
            final Runnable runnable = createProcess(countDownLatch);
            executorService.submit(runnable);
        }

        // wait for thread end
        countDownLatch.await();
        executorService.shutdown();

        // log elapsed time
        chrono.debug("[{}] time {}ms", getClass().getSimpleName(), System.currentTimeMillis()
            - start);
    }

    /**
     * must be &lt= hibernate.c3p0.max_size
     * 
     * @return the thread pool size
     */
    protected abstract int getThreadPoolSize();

    /**
     * create a new process.
     * @param countDownLatch the countDownLatch
     * @return a new process
     */
    protected abstract Runnable createProcess(final CountDownLatch countDownLatch);

    /**
     * register a {@code CourseStatusMXBean} as 
     * {@code demo.batch.course:type=getClass().getSimpleName()}.
     * 
     * @param courseStatusMXBean the course MBean
     */
    protected void registerMBean(final CourseStatusMXBean courseStatusMXBean) {
        try {
            final ObjectName name = new ObjectName("demo.batch.course:type="
                + getClass().getSimpleName());

            ManagementFactory.getPlatformMBeanServer().registerMBean(courseStatusMXBean, name);

        } catch (final Exception e) {
            logger.error("registerMBean", e);
        }
    }

    /**
     * unregister the course MBean.
     */
    protected void unregisterMBean() {
        try {
            final ObjectName name = new ObjectName("demo.batch.course:type="
                + getClass().getSimpleName());

            ManagementFactory.getPlatformMBeanServer().unregisterMBean(name);

        } catch (final Exception e) {
            logger.error("unregisterMBean", e);
        }
    }
}
