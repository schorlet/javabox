package demo.gap.shared.mem;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * GuiceJUnit4Runner
 */
public class GuiceJUnit4Runner extends BlockJUnit4ClassRunner {

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public @interface GuiceModules {
        Class<?>[] value();
    }

    protected final Injector injector;

    /**
     * @param klass
     * @throws InitializationError
     */
    public GuiceJUnit4Runner(final Class<?> klass) throws InitializationError {
        super(klass);

        final Class<?>[] classes = getModulesFor(klass);
        final Module[] modules = createModulesWith(classes);
        injector = Guice.createInjector(modules);
    }

    private Class<?>[] getModulesFor(final Class<?> klass) throws InitializationError {
        final GuiceModules annotation = klass.getAnnotation(GuiceModules.class);
        if (annotation == null)
            throw new InitializationError("Missing @GuiceModules annotation for unit test '"
                + klass.getName() + "'");
        return annotation.value();
    }

    private Module[] createModulesWith(final Class<?>[] classes) throws InitializationError {
        final Module[] modules = new Module[classes.length];
        for (int i = 0; i < classes.length; i++) {
            try {
                modules[i] = (Module) classes[i].newInstance();
            } catch (final Exception e) {
                throw new InitializationError(e);
            }
        }
        return modules;
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
}
