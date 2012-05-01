package demo.gap.shared.mem;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.RandomStringUtils;

import demo.gap.shared.domain.pojo.Activity;
import demo.gap.shared.domain.pojo.Gap;
import demo.gap.shared.domain.pojo.User;

/**
 * MemDomainUtil
 */
public class MemDomainUtil {

    public static Set<User> getUsers() {
        final Set<User> users = new HashSet<User>(USERS.length);
        for (final String user : USERS) {
            users.add(new User(user));
        }
        return users;
    }

    /*
     * 
     */
    public static Set<Gap> newGaps() {
        final Set<Gap> gaps = new HashSet<Gap>();

        for (final String version : VERSIONS) {
            gaps.add(newGap(randomGapId(), version));
        }
        return gaps;
    }

    public static Set<Gap> newGapsWithActivities() {
        return newGapsWithActivities(16, 20);
    }

    public static Set<Gap> newGapsWithActivities(final int min, final int max) {
        final Set<Gap> gaps = new HashSet<Gap>();

        for (final String version : VERSIONS) {
            final Gap newGap = newGap(randomGapId(), version);

            double min2 = Math.random() * max;
            min2 = min2 < min ? min : min2;
            final double ceil = Math.ceil(min2);
            for (int i = 0; i < ceil; i++) {
                newGap.add(newActivity(newGap));
            }

            gaps.add(newGap);
        }
        return gaps;
    }

    /*
     * 
     */
    public static Gap newGap() {
        return newGap(randomGapId(), randomVersion());
    }

    public static Gap newGap(final String id) {
        return newGap(id, randomVersion());
    }

    static Gap newGap(final String id, final String version) {
        return new Gap(id, version, RandomStringUtils.randomAlphabetic(20));
    }

    /*
     * 
     */
    public static Gap newGap(final Activity newActivity) {
        return copyGap(newActivity.getGap(), new HashSet<Activity>(Arrays.asList(newActivity)));
    }

    public static Gap newGap(final Activity... activities) {
        final Gap gap = newGap();

        for (final Activity activity : activities) {
            gap.add(activity);
        }

        return gap;
    }

    public static Gap newGap(final Set<Activity> activities) {
        return newGap(activities.toArray(new Activity[] {}));
    }

    public static Gap copyGap(final Gap gap1, final Set<Activity> activities) {
        return new Gap(gap1.getId(), gap1.getVersion(), gap1.getDescription(), activities);
    }

    /*
     * 
     */
    public static Activity newActivity() {
        return newActivity(newGap());
    }

    public static Activity newActivity(final Gap gap) {
        return new Activity(randomActivityId(), gap, randomUser(), randomPastDay(60), randomTime());
    }

    public static Set<Activity> newActivities(final Activity... newActivities) {
        final Set<Activity> list = new TreeSet<Activity>();
        for (final Activity activity : newActivities) {
            list.add(activity);
        }
        return list;
    }

    static final Calendar CALENDAR = Calendar.getInstance();
    static final long NOW = CALENDAR.getTimeInMillis();
    static final Random RANDOM = new Random();

    public static final String[] USERS = { "CHTS", "RCTC", "BHRY", "BRUG" };
    public static final String[] VERSIONS = { "1.7.0", "1.7.1", "1.7.2" };

    static String randomUser() {
        return USERS[RANDOM.nextInt(USERS.length)];
    }

    static String randomVersion() {
        return VERSIONS[RANDOM.nextInt(VERSIONS.length)];
    }

    static Float randomTime() {
        final double next = Math.random();
        if (next <= 0.2f) return 0.2f;
        if (next <= 0.4f) return 0.4f;
        if (next <= 0.6f) return 0.6f;
        if (next <= 0.8f) return 0.8f;
        return 1f;
    }

    static Date randomPastDay(final int min) {
        CALENDAR.setTimeInMillis(NOW);
        CALENDAR.add(Calendar.DAY_OF_YEAR, -RANDOM.nextInt(min));
        return CALENDAR.getTime();
    }

    public static Date nextDay(final Date d, final int dayofyear) {
        CALENDAR.setTime(d);
        CALENDAR.add(Calendar.DAY_OF_YEAR, dayofyear);
        return CALENDAR.getTime();
    }

    public static Date selectDay(final int dayofyear) {
        CALENDAR.setTimeInMillis(NOW);
        CALENDAR.add(Calendar.DAY_OF_YEAR, dayofyear);
        return CALENDAR.getTime();
    }

    public static String randomActivityId() {
        return RandomStringUtils.randomNumeric(6);
    }

    public static String randomGapId() {
        return "000" + RandomStringUtils.randomAlphanumeric(5).toUpperCase();
    }

    public static void print(final Set<Gap> gaps) {
        for (final Gap gap : gaps) {
            System.err.println(gap);
            printActivities(gap.getActivities());
        }
        System.err.println();
    }

    public static void printActivities(final Set<Activity> activities) {
        final Iterator<Activity> iterator = activities.iterator();
        while (iterator.hasNext()) {
            System.err.printf("  %s%n", iterator.next());
        }
        System.err.println();
    }

}
