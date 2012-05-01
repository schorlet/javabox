package demo.gap.shared.domain.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.gap.shared.domain.pojo.Activity;
import demo.gap.shared.domain.pojo.Gap;

/**
 * Filter
 */
public final class Filter {
    private String id;
    private String gapid;
    private String version;
    private String user;
    private Date day;
    private Date startDate;
    private Date endDate;

    public Filter() {
        this(null, null, null, null, null, null, null);
    }

    /**
     * @param id
     * @param user
     * @param version
     * @param day
     * @param startDate
     * @param endDate
     */
    Filter(final String id, final String gapid, final String user, final String version,
        final Date day, final Date startDate, final Date endDate) {
        this.id = id;
        this.gapid = gapid;
        this.user = user;
        this.version = version;
        this.day = day;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public String getGapId() {
        return gapid;
    }

    public String getVersion() {
        return version;
    }

    public String getUser() {
        return user;
    }

    public Date getDay() {
        return day;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        String comma = "Filter [";

        if (notNull(getId())) {
            builder.append(comma).append("id=").append(getId());
            comma = ",";
        }

        if (notNull(getGapId())) {
            builder.append(comma).append("gapid=").append(getGapId());
            comma = ",";
        }

        if (notNull(getUser())) {
            builder.append(comma).append("user=").append(getUser());
            comma = ",";
        }

        if (notNull(getVersion())) {
            builder.append(comma).append("version=").append(getVersion());
            comma = ",";
        }

        if (notNull(getDay())) {
            builder.append(comma).append("day=")
                .append(DateFormatUtils.ISO_DATE_FORMAT.format(getDay()));
            comma = ",";
        }

        if (notNull(getStartDate())) {
            builder.append(comma).append("startDate=")
                .append(DateFormatUtils.ISO_DATETIME_FORMAT.format(getStartDate()));
            comma = ",";
        }

        if (notNull(getEndDate())) {
            builder.append(comma).append("endDate=")
                .append(DateFormatUtils.ISO_DATETIME_FORMAT.format(getEndDate()));
            comma = ",";
        }

        if (builder.length() == 0) return "Filter []";
        return builder.append("]").toString();
    }

    public Filter byId(final String id) {
        this.id = id;
        return this;
    }

    public Filter byGapId(final String gapid) {
        this.gapid = gapid;
        return this;
    }

    public Filter byUser(final String user) {
        this.user = user;
        return this;
    }

    public Filter byVersion(final String version) {
        this.version = version;
        return this;
    }

    public Filter byDay(final Date day) {
        this.day = day;
        return this;
    }

    public Filter byDayInterval(final Date startDate, final Date endDate) {
        final Calendar calendar = Calendar.getInstance();

        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        this.startDate = calendar.getTime();

        calendar.setTime(endDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        this.endDate = calendar.getTime();

        return this;
    }

    public static boolean notNull(final Object o) {
        return o != null;
    }

    public static boolean equals(final Date a, final Date b) {
        return DateUtils.isSameDay(a, b);
    }

    public static boolean equals(final Object a, final Object b) {
        return a.equals(b);
    }

    public static boolean gte(final Date a, final Date b) {
        return b.after(a) || equals(a, b);
    }

    public boolean apply(final Activity activity) {
        logger.trace("apply {} on {}", this.toString(), activity);

        if (notNull(getId()) && activity.getId().equals(getId())) return true;
        else if (notNull(getId())) return false;

        boolean add = true;

        if (add && notNull(getGapId())) {
            add = equals(getGapId(), activity.getGap().getId());
        }

        if (add && notNull(getStartDate())) {
            add = gte(getStartDate(), activity.getDay());
        }

        if (add && notNull(getEndDate())) {
            add = gte(activity.getDay(), getEndDate());
        }

        if (add && notNull(getDay())) {
            add = equals(activity.getDay(), getDay());
        }

        if (add && notNull(getUser())) {
            add = equals(activity.getUser(), getUser());
        }

        if (add && notNull(getVersion())) {
            add = equals(activity.getVersion(), getVersion());
        }

        return add;
    }

    static final Logger logger = LoggerFactory.getLogger(Filter.class);

    public Set<Activity> activities(final Set<Activity> activities) {
        final Set<Activity> copy = new HashSet<Activity>();

        for (final Activity activity : activities) {
            final boolean add = apply(activity);

            if (add) {
                copy.add(activity);
            }

        }

        return copy;
    }

    public Set<Gap> gaps(final Set<Gap> gaps) {
        final Set<Gap> copy = new HashSet<Gap>();

        final boolean idIsNotNull = Filter.notNull(getId());
        final boolean versionIsNotNull = Filter.notNull(getVersion());

        for (final Gap gap : gaps) {
            final boolean add = apply(gap);

            if (add && idIsNotNull) {
                copy.add(gap);
                break;

            } else if (add) {
                copy.add(gap);

            } else if (!idIsNotNull && !versionIsNotNull) {
                copy.add(gap);

            } else if (!idIsNotNull && versionIsNotNull) {
                if (Filter.equals(getVersion(), gap.getVersion())) {
                    copy.add(gap);
                }
            }
        }

        return copy;
    }

    public boolean apply(final Gap gap) {
        logger.trace("apply {} on {}", this.toString(), gap);

        if (getId() != null && gap.getId().equals(getId())) return true;
        else if (getId() != null) {
            gap.clear();
            return false;
        }

        boolean add = true;

        if (add && notNull(getVersion())) {
            add = equals(gap.getVersion(), getVersion());
        }

        if (add && notNull(getUser())) {
            add = apply(gap, new Filter().byUser(getUser()));
        }

        if (add && notNull(getDay())) {
            add = apply(gap, new Filter().byDay(getDay()));
        }

        if (add && notNull(getStartDate()) && notNull(getEndDate())) {
            add = apply(gap, new Filter().byDayInterval(getStartDate(), getEndDate()));
        }

        if (!add) {
            gap.clear();
        }

        return add;
    }

    private boolean apply(final Gap gap, final Filter filter) {
        final Set<Activity> activities = gap.getByFilter(filter);
        if (activities.isEmpty()) return false;

        gap.clear();
        gap.addAll(activities);
        return true;
    }

    public static void merge(final Set<Gap> gaps, final Set<Activity> activities) {
        final Map<String, Gap> map = new HashMap<String, Gap>();
        for (final Gap gap : gaps) {
            map.put(gap.getId(), gap);
            gap.clear();
        }

        for (final Activity activity : activities) {
            map.get(activity.getGap().getId()).add(activity);
        }
    }

    public static void merge(final Set<Gap> gaps, final Activity activity) {
        final Gap gap = activity.getGap();
        if (gaps.remove(gap)) {
            gap.clear();
            gap.add(activity);
            gaps.add(gap);
        }
    }

    public static void merge(final Gap gap, final Activity activity) {
        gap.clear();
        gap.add(activity);
    }

}
