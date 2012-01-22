package demo.hello.client;

import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import com.google.gwt.logging.client.HasWidgetsLogHandler;
import com.google.gwt.logging.client.HtmlLogFormatter;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.logging.client.TextLogFormatter;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

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

        return java.util.logging.Logger.getLogger("demo.hello.client");
    }

    /**
     * addHandler
     * @param hasWidgetsLogHandler
     */
    public static void addHandler(final HasWidgetsLogHandler hasWidgetsLogHandler) {
        java.util.logging.Logger.getLogger("").addHandler(hasWidgetsLogHandler);
        hasWidgetsLogHandler.setFormatter(new HtmlFormatter());
    }

    /**
     * removeHandler
     * @param hasWidgetsLogHandler
     */
    public static void removeHandler(final HasWidgetsLogHandler hasWidgetsLogHandler) {
        java.util.logging.Logger.getLogger("").removeHandler(hasWidgetsLogHandler);
    }

    /**
     * logp
     */
    public static void logp(final Level level, final String sourceClass, final String sourceMethod,
        final Object... args) {

        if (LogConfiguration.loggingIsEnabled()) {
            if (!logger.isLoggable(level)) return;

            final StringBuilder sb = new StringBuilder(sourceClass).append(".")
                .append(sourceMethod).append(" - ");

            if (args.length > 0) {
                for (final Object arg : args) {
                    sb.append(String.valueOf(arg)).append(", ");
                }
                sb.setLength(sb.length() - 1);
            }

            log(level, sourceClass, sourceMethod, sb.toString());
        }
    }

    /**
     * logpf
     */
    public static void logpf(final Level level, final String sourceClass,
        final String sourceMethod, final String format, final Object... args) {

        if (LogConfiguration.loggingIsEnabled()) {
            if (!logger.isLoggable(level)) return;

            final String message = format(format, args);

            final StringBuilder sb = new StringBuilder(sourceClass).append(".")
                .append(sourceMethod).append(" - ").append(message);

            log(level, sourceClass, sourceMethod, sb.toString());
        }
    }

    /**
     * log
     */
    public static void log(final Level level, final String sourceClass, final String sourceMethod,
        final String message) {

        if (LogConfiguration.loggingIsEnabled()) {
            if (!logger.isLoggable(level)) return;
            logger.log(level, message);
        }
    }

    /**
     * loge
     */
    public static void loge(final Level level, final String sourceClass, final String sourceMethod,
        final Throwable throwable) {

        if (LogConfiguration.loggingIsEnabled()) {
            if (!logger.isLoggable(level)) return;
            logger.log(level, String.valueOf(throwable), throwable);
        }
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
        final RegExp regex = RegExp.compile("%(\\d+)?(.(\\d+))?([a-z]+)", "gi");
        final StringBuilder sb = new StringBuilder();

        int fromIndex = 0;
        final int length = format.length();

        int argsIndex = 0;

        while (fromIndex < length && argsIndex < args.length) {
            // Find the next match of the highlight regex
            final MatchResult result = regex.exec(format);
            if (result == null) {
                break;
            }

            final int index = result.getIndex();
            final String match = result.getGroup(0);

            final String group1 = result.getGroup(1);
            final Integer width = group1 == null ? null : Integer.valueOf(group1);

            final String group3 = result.getGroup(3);
            final Integer precision = group3 == null ? null : Integer.valueOf(group3);

            final String conversion = result.getGroup(4);

            // Append the characters leading up to the match
            sb.append(format.substring(fromIndex, index));

            // date conversion
            if ("t".equalsIgnoreCase(conversion) || "tt".equalsIgnoreCase(conversion)) {
                final Date arg = (Date) args[argsIndex++];
                final String arg_value;

                if (arg == null) {
                    arg_value = "null";

                } else if ("t".equals(conversion)) {
                    arg_value = DATE_FORMAT.format(arg);

                } else if ("T".equals(conversion)) {
                    arg_value = TIME_FORMAT.format(arg);

                } else {
                    arg_value = DATE_TIME_FORMAT.format(arg);
                }

                sb.append(arg_value);
            }
            // decimal conversion
            else if ("f".equals(conversion)) {
                final Double arg = (Double) args[argsIndex++];
                final String arg_value;

                if (arg == null || precision == null) {
                    arg_value = String.valueOf(arg);

                } else {
                    final String arg_format = "#,##0.".concat(repeat("0", precision - 1)).concat(
                        "#");
                    arg_value = NumberFormat.getFormat(arg_format).format(arg);
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
            fromIndex = index + match.length();
            regex.setLastIndex(fromIndex);
        }

        // Append the tail of the string
        if (fromIndex < length) {
            sb.append(format.substring(fromIndex));
        }

        return sb.toString();
    }

    static final DateTimeFormat TIME_FORMAT = DateTimeFormat
        .getFormat(PredefinedFormat.TIME_MEDIUM);

    static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);

    static final DateTimeFormat DATE_TIME_FORMAT = DateTimeFormat
        .getFormat(PredefinedFormat.DATE_TIME_MEDIUM);

    static String repeat(final String s, final Integer count) {
        if (count == null || count <= 0) return "";

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    static class HtmlFormatter extends HtmlLogFormatter {
        public HtmlFormatter() {
            super(true);
        }

        final Date date = new Date();
        final StringBuilder sb = new StringBuilder();

        @Override
        protected String getRecordInfo(final LogRecord record, final String newline) {
            date.setTime(record.getMillis());
            sb.setLength(0);

            sb.append("<span style='color:lightyellow'>");
            sb.append(TIME_FORMAT.format(date)).append(" ");
            sb.append("</span>");

            return sb.toString();
        }
    }

    static class TextFormatter extends TextLogFormatter {
        public TextFormatter() {
            super(true);
        }

        final Date date = new Date();
        final StringBuilder sb = new StringBuilder();

        @Override
        protected String getRecordInfo(final LogRecord record, final String newline) {
            date.setTime(record.getMillis());
            sb.setLength(0);

            sb.append(DATE_TIME_FORMAT.format(date)).append(" ");
            return sb.toString();
        }
    }

}
