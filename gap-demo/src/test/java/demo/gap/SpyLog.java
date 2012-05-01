package demo.gap;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SpyLog
 */
public class SpyLog {
    private static final Pattern SPYLOG = Pattern
        .compile("^(.*)\\|(.*)\\|(.*)\\|(.*)\\|(.*)\\|(.*)$");

    public static void main(final String[] args) {
        echo();
    }

    public static void echo() {
        try {
            echo(new File("spy.log"));
        } catch (final Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void echo(final File file) throws IOException, InterruptedException {
        final Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            process(scanner.nextLine());
        }
        scanner.close();
    }

    private static void process(final String line) {
        final Matcher match = SPYLOG.matcher(line);
        if (!match.find()) return;

        final String category = match.group(4);

        if ("statement".equals(category)) {
            final String statement = match.group(6);
            System.err.printf("p6spy - %s%n", statement);

        } else if ("commit".equals(category) || "rollback".equals(category)) {
            System.err.printf("p6spy - %s%n", category);
        }
    }

}
