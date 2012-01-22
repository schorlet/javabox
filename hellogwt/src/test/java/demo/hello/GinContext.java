package demo.hello;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.google.gwt.inject.client.Ginjector;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * GinContext. NOT IN USE !!
 * 
 * Builds an implementation of the GinInjector which does not require a GWT runtime environment.
 * 
 * http://howtogwt.blogspot.com/2010/03/instance-creation-and-dependency.html
 * https://groups.google.com/group/google-gin/browse_thread/thread/8d1fe3809bc9b9fb
 */
class GinContext {

    /**
     * make a proxy of the guiceModule into a ginInjector.
     */
    public static <T extends Ginjector> T guiceCreate(final Module guiceModule,
        final Class<T> ginInjector) {
        final Injector guiceInjector = Guice.createInjector(guiceModule);
        return getServerSideGinInjector(guiceInjector, ginInjector);
    }

    /**
     * make a proxy of the guiceInjector into a ginInjector.
     */
    @SuppressWarnings("unchecked")
    public static <T extends Ginjector> T getServerSideGinInjector(final Injector guiceInjector,
        final Class<T> ginInjector) {

        final Object result = Proxy.newProxyInstance(Injector.class.getClassLoader(),
            new Class[] { ginInjector }, new InvocationHandler() {

                @Override
                public Object invoke(final Object proxy, final Method method, final Object[] args)
                    throws Throwable {

                    final Class<?> returnType = method.getReturnType();
                    return guiceInjector.getInstance(returnType);
                }
            });

        return (T) result;
    }

}
