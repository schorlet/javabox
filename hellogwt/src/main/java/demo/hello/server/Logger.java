package demo.hello.server;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Logger
 */
public class Logger {
    static final java.util.logging.Logger logger = initializeLogging();

    private static java.util.logging.Logger initializeLogging() {
        final Handler[] handlers = java.util.logging.Logger.getLogger("").getHandlers();
        if (handlers != null && handlers.length > 0) {
            for (final Handler handler : handlers) {
                handler.setFormatter(new TextFormatter());
            }
        }

        return java.util.logging.Logger.getLogger("demo.hello.server");
    }

    /**
     * logp
     */
    public static void logp(final Level level, final String sourceClass, final String sourceMethod,
        final Object... args) {

        if (!logger.isLoggable(level)) return;

        final StringBuilder sb = new StringBuilder(sourceClass).append(".").append(sourceMethod)
            .append(" - ");

        if (args.length > 0) {
            for (final Object arg : args) {
                sb.append(String.valueOf(arg)).append(", ");
            }
            sb.setLength(sb.length() - 1);
        }

        log(level, sourceClass, sourceMethod, sb.toString());
    }

    /**
     * logpf
     */
    public static void logpf(final Level level, final String sourceClass,
        final String sourceMethod, final String format, final Object... args) {

        if (!logger.isLoggable(level)) return;

        final String message = format(format, args);

        final StringBuilder sb = new StringBuilder(sourceClass).append(".").append(sourceMethod)
            .append(" - ").append(message);

        log(level, sourceClass, sourceMethod, sb.toString());
    }

    /**
     * log
     */
    public static void log(final Level level, final String sourceClass, final String sourceMethod,
        final String message) {

        if (!logger.isLoggable(level)) return;
        logger.log(level, message);
    }

    /**
     * loge
     */
    public static void loge(final Level level, final String sourceClass, final String sourceMethod,
        final Throwable throwable) {

        if (!logger.isLoggable(level)) return;
        logger.log(level, String.valueOf(throwable), throwable);
    }

    /**
     * format: %[width][.precision]conversion<br/>
     * conversion: s | f | (t | T | tT)<br/><br/>
     * format("%2s %2s %2s %2s", "a", "b", "c", "d")</br>
     *  -> " a b c d"<br/><br/>
     * format("e = %10.4f", Math.E)<br/>
     *  -> "e = 2,7183"<br/><br/>
     * format("date = %t", today)<br/>
     *  -> "date = 12/18/07"<br/><br/>
     * format("time = %T", today)<br/>
     *  -> "time = 12:01:26"<br/><br/>
     * format("datetime = %tT", today)<br/>
     *  -> "datetime = 12/18/07 12:01:26"<br/><br/>
     */
    public static String format(final String format, final Object... args) {
        final Pattern regex = Pattern.compile("%(\\d+)?(\\.(\\d+))?(s|f|t{1,2})",
            Pattern.CASE_INSENSITIVE);
        final StringBuilder sb = new StringBuilder();

        int fromIndex = 0;
        int argsIndex = 0;

        final int length = format.length();
        final Matcher matcher = regex.matcher(format);

        while (fromIndex < length && argsIndex < args.length) {
            // Find the next match of the highlight regex
            if (!matcher.find(fromIndex)) {
                break;
            }

            final int index = matcher.start();

            final String group1 = matcher.group(1);
            final Integer width = group1 == null ? null : Integer.valueOf(group1);

            final String group3 = matcher.group(3);
            final Integer precision = group3 == null ? null : Integer.valueOf(group3);

            final String conversion = matcher.group(4);

            // Append the characters leading up to the match
            sb.append(format.substring(fromIndex, index));

            // date conversion
            if ("t".equalsIgnoreCase(conversion) || "tt".equalsIgnoreCase(conversion)) {
                final Date arg = (Date) args[argsIndex++];
                final String arg_value;

                if (arg == null) {
                    arg_value = "null";

                } else if ("t".equals(conversion)) {
                    DATE_FORMAT.applyPattern("yyyy-MM-dd");
                    arg_value = DATE_FORMAT.format(arg);

                } else if ("T".equals(conversion)) {
                    DATE_FORMAT.applyPattern("HH:mm:ss");
                    arg_value = DATE_FORMAT.format(arg);

                } else {
                    DATE_FORMAT.applyPattern("yyyy-MM-dd HH:mm:ss");
                    arg_value = DATE_FORMAT.format(arg);
                }

                sb.append(arg_value);
            }
            // decimal conversion
            else if ("f".equals(conversion)) {
                final Object arg = args[argsIndex++];
                final String arg_value;

                if (arg == null || precision == null) {
                    arg_value = String.valueOf(arg);

                } else {
                    NUMBER_FORMAT.setMinimumFractionDigits(precision);
                    NUMBER_FORMAT.setMaximumFractionDigits(precision);
                    arg_value = NUMBER_FORMAT.format(arg);
                }

                sb.append(arg_value);
            }
            // string conversion
            else {
                String arg = String.valueOf(args[argsIndex++]);
                if (arg != null && width != null) {
                    final int nb_space = width - arg.length();
                    arg = arg.concat(repeat(" ", nb_space));
                }
                sb.append(arg);
            }

            // Skip past the matched string
            fromIndex = matcher.end();
        }

        // Append the tail of the string
        if (fromIndex < length) {
            sb.append(format.substring(fromIndex));
        }

        return sb.toString();
    }

    static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.ENGLISH);
    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat();

    static String repeat(final String s, final Integer count) {
        if (count == null || count <= 0) return "";

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    static class TextFormatter extends Formatter {
        private static final String LINE_SEPARATOR = System.getProperty("line.separator");

        final Date date = new Date();
        final StringBuilder sb = new StringBuilder();

        @Override
        public String format(final LogRecord record) {
            date.setTime(record.getMillis());
            sb.setLength(0);

            DATE_FORMAT.applyPattern("HH:mm:ss");
            sb.append(DATE_FORMAT.format(date));
            sb.append(": ").append(formatMessage(record)).append(LINE_SEPARATOR);
            return sb.toString();
        }
    }

}
