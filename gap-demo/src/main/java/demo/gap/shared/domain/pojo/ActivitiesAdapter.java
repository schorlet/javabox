package demo.gap.shared.domain.pojo;

import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * ActivitiesAdapter
 */
public class ActivitiesAdapter extends XmlAdapter<Activity[], Set<Activity>> {
    @Override
    public Activity[] marshal(final Set<Activity> activities) throws Exception {
        if (activities == null) return new Activity[0];
        final Activity[] activities2 = new Activity[activities.size()];
        return activities.toArray(activities2);
    }

    @Override
    public Set<Activity> unmarshal(final Activity[] elements) throws Exception {
        final Set<Activity> activities = new TreeSet<Activity>();
        for (final Activity activity : elements) {
            activities.add(activity);
        }

        return activities;
    }

}
