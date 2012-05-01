package demo.gap.server.jpa;

import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.PersistService;

import demo.gap.shared.mem.GuiceJUnit4Runner;

/**
 * GuiceJpaJUnit4Runner
 */
public class GuiceJpaJUnit4Runner extends GuiceJUnit4Runner {
    final Logger logger = LoggerFactory.getLogger(GuiceJpaJUnit4Runner.class);

    /**
     * @param klass
     * @throws InitializationError
     */
    public GuiceJpaJUnit4Runner(final Class<?> klass) throws InitializationError {
        super(klass);
        logger.debug("STARTING");
        injector.getInstance(PersistService.class).start();
    }

    @Override
    public void run(final RunNotifier notifier) {
        notifier.addListener(new RunFinished());
        super.run(notifier);
    }

    /**
    * RunFinished
    */
    class RunFinished extends RunListener {
        @Override
        public void testRunFinished(final Result result) throws Exception {
            logger.debug("STOP");
            injector.getInstance(PersistService.class).stop();

            // SpyLog.echo();

            super.testRunFinished(result);
        }
    }

}
