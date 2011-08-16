package demo.batch.course;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Process
 */
public abstract class Process extends Perform implements Runnable {
    static final AtomicInteger processNumber = new AtomicInteger();

    final CountDownLatch countDownLatch;

    final PlatformTransactionManager transactionManager;

    TransactionStatus transactionStatus;

    public Process(final CountDownLatch countDownLatch,
        final PlatformTransactionManager transactionManager) {

        this.countDownLatch = countDownLatch;
        this.transactionManager = transactionManager;
    }

    int processId = 0;

    /**
     * the unique process id.
     * 
     * @return the unique process id
     */
    public final int getProcessId() {
        return processId;
    }

    @Override
    public final void run() {
        execute();
    }

    @Override
    protected void performBefore() {
        processId = processNumber.incrementAndGet();
        logger.info("[process {}] begin {}", processId, Thread.currentThread().getName());

        final DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        transactionDefinition.setName(String.format("Process-%d", processId));

        transactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        transactionStatus = transactionManager.getTransaction(transactionDefinition);
    }

    @Override
    protected void performAfter() {
        countDownLatch.countDown();
    }

    @Override
    protected void fail(final Exception e) {
        logger.error("[process " + processId + "] exception", e);

        if (transactionStatus != null) {
            logger.info("[process {}] rollback {}", processId, Thread.currentThread().getName());
            transactionManager.rollback(transactionStatus);
            transactionStatus = null;
        }
    }

    @Override
    protected void success() {
        if (transactionStatus != null) {
            logger.info("[process {}] commit {}", processId, Thread.currentThread().getName());
            transactionManager.commit(transactionStatus);
            transactionStatus = null;
        }
    }

    @Override
    public String toString() {
        return String.format("[process %d] %s", processId, Thread.currentThread().getName());
    }

}
