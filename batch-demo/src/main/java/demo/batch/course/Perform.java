package demo.batch.course;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Perform
 */
public abstract class Perform {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final Logger chrono = LoggerFactory.getLogger("demo.chrono");

    /**
     * execute the perform flow
     */
    public final void execute() {
        try {
            performBefore();
            perform();
            success();

        } catch (final Exception e) {
            fail(e);

        } finally {
            performAfter();
        }
    }

    /**
     * before perform
     */
    protected abstract void performBefore();

    /**
     * perform 
     * @throws Exception
     */
    protected abstract void perform() throws Exception;

    /**
     * after perform
     */
    protected abstract void performAfter();

    /**
     * success
     */
    protected void success() {}

    /**
     * {@code fail()} will be called if {@code succes()} throws an exception.
     */
    protected void fail(final Exception e) {
        logger.error("fail", e);
    }

}
