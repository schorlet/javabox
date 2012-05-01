package demo.gap.shared.domain.service;

import java.util.Set;

import demo.gap.shared.domain.pojo.Activity;

/**
 * ActivityService
 */
public interface ActivityService {

    // read

    /**
     * test activities list
     */
    boolean isEmpty();

    /**
     * get activity with the specified id
     * @param id
     */
    Activity getById(String id);

    /**
     * get activity with the specified gapid
     * @param id
     */
    Set<Activity> getByGapId(String gapid);

    /**
     * get all activities
     */
    Set<Activity> getActivities();

    /**
     * get activities matching the specified filter
     * @param filter
     */
    Set<Activity> getByFilter(Filter filter);

    // write

    /**
     * addAll activities
     * @param activities
     */
    void addAll(Set<Activity> activities);

    /**
     * add the specified activity
     * @param activity
     */
    void add(Activity activity);

    /**
     * remove the specified activity
     * @param activity
     */
    boolean remove(Activity activity);

    /**
     * clear all activities
     */
    void clear();

}
