package demo.hello;

import java.util.List;

import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;

/**
 * GuiceJUnit4Runner
 */
public class GuiceJUnit4Runner extends BlockJUnit4ClassRunner {

    private final Injector injector;

    /**
     * @param klass
     * @throws InitializationError
     */
    public GuiceJUnit4Runner(final Class<?> klass) throws InitializationError {
        super(klass);

        injector = Guice.createInjector(new GuiceTestModule());

        // start PersistService
        injector.getInstance(PersistService.class).start();
    }

    @Override
    protected Object createTest() {
        return injector.getInstance(getTestClass().getJavaClass());
    }

    @Override
    protected void validateZeroArgConstructor(final List<Throwable> errors) {
        // Guice can inject constructors with parameters
        // so we don't want this method to trigger an error
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

            // stop PersistService
            injector.getInstance(PersistService.class).stop();

            super.testRunFinished(result);
        }
    }

}
