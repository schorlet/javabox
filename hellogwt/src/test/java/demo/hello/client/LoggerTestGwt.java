package demo.hello.client;

import java.util.Date;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * LoggerTestGwt
 */
public class LoggerTestGwt extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "demo.hello.ModuleTest";
    }

    final Date date = new Date(1321655233629l);

    public void test1() {
        final Object[] args = { "abc", "def", 2.456789f, 3.123456d, date, date, date };
        final String test = Logger.format("%10s, %s, (%2.3f), %f, %t, %T, %tT, %s", args);
        System.out.println(test);

        assertEquals(
            "abc       , def, (2.457), 3.123456, 2011-11-18, 23:27:13, 2011-11-18 23:27:13, %s",
            test);
    }

    public void test2() {
        final Object[] args = new Object[4];
        final String test = Logger.format("%s, %f, %t, %u, %s", args);
        System.out.println(test);

        assertEquals("null, null, null, %u, null", test);
    }
}
