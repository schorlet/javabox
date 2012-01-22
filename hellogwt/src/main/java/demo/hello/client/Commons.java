package demo.hello.client;

import java.util.Date;

/**
 * Commons
 */
public class Commons {
    static final char[] CHARS = { 'A', 'b', 'C', 'd', 'E', 'f', '1', '2', '3', '@' };

    public static String nextString(final int length) {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(CHARS[(int) (Math.random() * CHARS.length)]);
        }

        return sb.toString();
    }

    public static double nextDouble(final int max) {
        return Math.random() * max;
    }

    public static boolean nextBoolean() {
        return Math.round(Math.random()) == 0;
    }

    public static Date nextDate() {
        final long currentTimeMillis = System.currentTimeMillis();
        return new Date(new Double(Math.random() * currentTimeMillis).longValue());
    }
}
