package demo.hello.client;

import junit.framework.Test;

import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.gwt.junit.tools.GWTTestSuite;

/**
 * GwtTestSuite
 */
public class GwtTestSuite {
    public static Test suite() {
        // jul-to-slf4j bridge
        SLF4JBridgeHandler.install();

        final GWTTestSuite suite = new GWTTestSuite("demo.hello.client.GwtTestSuite");
        suite.addTestSuite(LoggerTestGwt.class);
        return suite;
    }
}
