package demo.gap.shared.mem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import demo.gap.shared.domain.pojo.Activity;
import demo.gap.shared.domain.pojo.Gap;
import demo.gap.shared.domain.service.ActivityService;
import demo.gap.shared.domain.service.Filter;
import demo.gap.shared.domain.service.GapService;

/**
 * MemGapService
 */
public class MemActivityService implements ActivityService {
    static final Logger logger = LoggerFactory.getLogger(MemActivityService.class);

    final GapService gapService;

    @Inject
    MemActivityService(final GapService gapService) {
        this.gapService = gapService;
    }

    @Override
    public boolean isEmpty() {
        logger.trace("isEmpty");
        return getActivities().isEmpty();
    }

    @Override
    public Set<Activity> getActivities() {
        logger.trace("getActivities");

        final Set<Activity> activities = new HashSet<Activity>();

        for (final Gap gap : gapService.getGaps()) {
            activities.addAll(gap.getActivities());
        }

        return activities;
    }

    @Override
    public Activity getById(final String id) {
        logger.trace("getById {}", id);

        final Set<Activity> byFilter = getByFilter(new Filter().byId(id));

        if (byFilter.isEmpty()) return null;
        else return byFilter.iterator().next();
    }

    @Override
    public Set<Activity> getByGapId(final String gapid) {
        logger.trace("getByGapId {}", gapid);
        return getByFilter(new Filter().byGapId(gapid));
    }

    @Override
    public Set<Activity> getByFilter(final Filter filter) {
        logger.trace("getByFilter {}", filter);

        final Set<Activity> copy = new HashSet<Activity>();
        final boolean idIsNotNull = Filter.notNull(filter.getId());

        for (final Activity activity : getActivities()) {
            final boolean add = filter.apply(activity);

            if (add && idIsNotNull) {
                copy.add(activity);
                break;

            } else if (add) {
                copy.add(activity);
            }
        }

        return copy;
    }

    @Override
    public void addAll(final Set<Activity> activities) {
        logger.trace("addAll {}", activities);

        final Map<String, Gap> map = new HashMap<String, Gap>();
        final Set<Gap> gaps = gapService.getGaps();
        for (final Gap gap : gaps) {
            map.put(gap.getId(), gap);
        }

        for (final Activity activity : activities) {
            if (!gaps.contains(activity.getGap()))
                throw new IllegalArgumentException("Unkown gap:" + activity.getGap());
            map.get(activity.getGap().getId()).add(activity);
        }
    }

    @Override
    public void add(final Activity activity) {
        logger.trace("add {}", activity);

        final Set<Gap> gaps = gapService.getGaps();

        for (final Gap gap : gaps) {
            if (gap.equals(activity.getGap())) {
                gap.add(activity);
                break;
            }
        }
    }

    @Override
    public boolean remove(final Activity activity) {
        logger.trace("remove {}", activity);

        for (final Gap gap : gapService.getGaps()) {
            if (gap.equals(activity.getGap())) return gap.remove(activity);
        }

        return false;
    }

    @Override
    public void clear() {
        logger.trace("clear");

        for (final Gap gap : gapService.getGaps()) {
            gap.clear();
        }
    }

}
