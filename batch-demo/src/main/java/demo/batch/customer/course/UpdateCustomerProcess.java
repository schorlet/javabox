package demo.batch.customer.course;

import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.transaction.PlatformTransactionManager;

import demo.batch.course.Process;
import demo.batch.customer.Repositories;

/**
 * UpdateCustomerProcess
 */
class UpdateCustomerProcess extends Process {
    final Repositories repositories;
    final Queue<List<String>> workQueue;

    public UpdateCustomerProcess(final Queue<List<String>> workQueue,
        final Repositories repositories, final PlatformTransactionManager transactionManager,
        final CountDownLatch countDownLatch) {

        super(countDownLatch, transactionManager);

        this.workQueue = workQueue;
        this.repositories = repositories;
    }

    @Override
    protected void perform() {
        while (true) {
            final List<String> poll = workQueue.poll();

            if (poll == null) return;

            logger.info("[process {}] poll {}", getProcessId(), poll.size());

            // start time
            final long start = System.currentTimeMillis();

            for (final String pollId : poll) {
                // update customer name
                updateCustomerName(pollId);
            }

            // log elapsed time
            chrono.debug("[process {}] {}ms", getProcessId(), System.currentTimeMillis() - start);
        }

    }

    void updateCustomerName(final String pollId) {
        final UUID uuid = UUID.fromString(pollId);

        logger.debug("[process {}] update customer {}", getProcessId(), pollId);

        // repositories.getCustomerRepository().updateName(uuid,
        // "abc " + RandomStringUtils.randomAlphanumeric(16));

        repositories.getCustomerRepository().updateNameSlow(uuid,
            "abc " + RandomStringUtils.randomAlphanumeric(16));
    }
}
