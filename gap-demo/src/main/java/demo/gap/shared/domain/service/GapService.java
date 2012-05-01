package demo.gap.shared.domain.service;

import java.util.Set;

import demo.gap.shared.domain.pojo.Gap;

/**
 * GapService
 */
public interface GapService {

    Set<String> getVersions();

    // read

    /**
     * test gaps list
     */
    boolean isEmpty();

    /**
     * return gaps size
     */
    int size();

    /**
     * get gap with the specified id
     * @param id
     */
    Gap getById(String id);

    /**
     * get all gaps
     */
    Set<Gap> getGaps();

    /**
     * get gaps matching the specified filter
     * @param filter
     */
    Set<Gap> getByFilter(Filter filter);

    // write

    /**
     * addAll gaps
     * @param gaps
     */
    void addAll(Set<Gap> gaps);

    /**
     * add the specified gap
     * @param gap
     */
    void add(Gap gap);

    /**
     * remove the specified gap
     * @param gap
     */
    boolean remove(Gap gap);

    /**
     * clear all gaps
     */
    void clear();

}
