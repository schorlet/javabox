package demo.gap.shared.domain;

import java.util.Date;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.gap.shared.domain.pojo.Activity;
import demo.gap.shared.domain.pojo.Gap;
import demo.gap.shared.domain.pojo.User;
import demo.gap.shared.domain.service.ActivityService;
import demo.gap.shared.domain.service.Filter;
import demo.gap.shared.domain.service.GapService;
import demo.gap.shared.domain.service.UserService;
import demo.gap.shared.mem.MemDomainUtil;
import demo.gap.shared.mem.SampleData;

/**
 * DomainServiceTest
 */
public abstract class DomainServiceTest {
    final Logger logger = LoggerFactory.getLogger(getClass());

    protected final GapService gapService;
    protected final ActivityService activityService;
    protected final UserService userService;

    protected DomainServiceTest(final GapService gapService, final ActivityService activityService,
        final UserService userService) {
        this.gapService = gapService;
        this.activityService = activityService;
        this.userService = userService;
    }

    @Before
    public void before() {
        logger.debug("before");

        gapService.clear();
        Assert.assertTrue(gapService.isEmpty());

        userService.clear();
        Assert.assertTrue(userService.isEmpty());

        userService.addAll(MemDomainUtil.getUsers());
    }

    @After
    public void after() {}

    @Test
    public void testGap() {
        logger.debug("testGap");

        final Set<Gap> newGaps = MemDomainUtil.newGaps();
        Assert.assertEquals(3, newGaps.size());
        gapService.addAll(newGaps);
        Assert.assertFalse(gapService.isEmpty());

        final Set<Gap> gaps = gapService.getGaps();
        Assert.assertEquals(3, gaps.size());
        Assert.assertEquals(newGaps, gaps);

        final Set<Gap> byFilter = gapService.getByFilter(new Filter());
        Assert.assertEquals(gaps, byFilter);

        final Gap newGap = MemDomainUtil.newGap();
        gapService.add(newGap);

        final Set<Gap> gaps2 = gapService.getGaps();
        Assert.assertEquals(4, gaps2.size());
    }

    @Test
    public void testActivity() {
        logger.debug("testActivity");

        final Activity newActivity = MemDomainUtil.newActivity();
        final Gap newGap = newActivity.getGap();
        gapService.add(newGap);
        activityService.add(newActivity);

        final Set<Gap> byVersion = gapService.getByFilter(new Filter().byVersion(newGap
            .getVersion()));
        Filter.merge(byVersion, newActivity);

        Assert.assertTrue(CollectionUtils.exists(byVersion, new Predicate() {
            @Override
            public boolean evaluate(final Object object) {
                return ((Gap) object).getById(newActivity.getId()) != null;
            }
        }));

        final Activity activity = activityService.getById(newActivity.getId());
        Assert.assertEquals(newActivity, activity);

        final Set<Activity> byGapId = activityService.getByGapId(newGap.getId());
        Assert.assertNotNull(byGapId);
        Assert.assertFalse(byGapId.isEmpty());
        Assert.assertEquals(1, byGapId.size());
        Assert.assertEquals(byGapId.iterator().next(), activity);

        final Gap gap = gapService.getById(newGap.getId());
        Filter.merge(gap, activity);
        Assert.assertFalse(gap.isEmpty());
        Assert.assertEquals(1, gap.getActivities().size());
        Assert.assertEquals(gap.iterator().next(), activity);
    }

    @Test
    public void testMerge() {
        logger.debug("testMerge");

        SampleData.reset(gapService, activityService, userService);

        final Set<User> users = userService.getUsers();
        final User user = users.iterator().next();

        final Set<String> versions = gapService.getVersions();
        final String version = versions.iterator().next();

        final Filter filter = new Filter().byUser(user.getUser()).byVersion(version);

        final Date fromDay = MemDomainUtil.selectDay(-20);
        final Date toDay = MemDomainUtil.selectDay(0);
        filter.byDayInterval(fromDay, toDay);

        final Set<Gap> gaps = gapService.getByFilter(filter);
        final Set<Activity> activities = activityService.getByFilter(filter);
        Filter.merge(gaps, activities);

        // final Set<Gap> gaps2 = filter.gaps(gaps);

        for (final Gap gap : gaps) {
            Assert.assertEquals(version, gap.getVersion());
            final Set<Activity> set = gap.getActivities();

            if (filter.apply(gap)) {
                for (final Activity activity : set) {
                    Assert.assertEquals(user.getUser(), activity.getUser());
                    Assert.assertEquals(gap, activity.getGap());
                    Assert.assertEquals(gap.getVersion(), activity.getVersion());
                    Assert.assertTrue(Filter.gte(fromDay, activity.getDay()));
                    Assert.assertTrue(Filter.gte(activity.getDay(), toDay));
                }
            } else {
                Assert.assertTrue(set.isEmpty());
            }
        }
    }

    @Test
    public void testCrud() {
        logger.debug("test4");

        final Gap gap = MemDomainUtil.newGap();
        final Activity activity1 = MemDomainUtil.newActivity(gap);
        final Activity activity2 = MemDomainUtil.newActivity(gap);
        gapService.add(gap);
        activityService.add(activity1);
        activityService.add(activity2);

        final Activity byId1 = activityService.getById(activity1.getId());
        Assert.assertNotNull(byId1);

        final Activity byId2 = activityService.getById(activity2.getId());
        Assert.assertNotNull(byId2);

        // test delete activity
        Assert.assertTrue(activityService.remove(byId1));

        final Activity removedId1 = activityService.getById(byId1.getId());
        Assert.assertEquals(null, removedId1);

        // test remove activity from gap
        final Gap gap2 = gapService.getById(gap.getId());
        Filter.merge(gap2, activity2);
        Assert.assertTrue(gap2.remove(activity2));

        // test add activity
        final Activity activity3 = MemDomainUtil.newActivity(gap2);
        activityService.add(activity3);

        final Gap gap3 = gapService.getById(gap2.getId());
        Filter.merge(gap3, activity3);
        Assert.assertEquals(1, gap3.getActivities().size());
        Assert.assertNotNull(gap3.getById(activity3.getId()));

        // test remove gap
        Assert.assertTrue(gapService.remove(gap3));

        final Gap gap5 = gapService.getById(gap3.getId());
        Assert.assertEquals(null, gap5);

        final Activity removedId3 = activityService.getById(activity3.getId());
        Assert.assertEquals(null, removedId3);
    }

    @Test
    public void testFilter2() {
        logger.debug("testFilter2");

        final Gap gap = MemDomainUtil.newGap();
        final String version = gap.getVersion();
        gapService.add(gap);

        final Set<String> versions = gapService.getVersions();
        Assert.assertNotNull(versions);
        Assert.assertEquals(1, versions.size());
        Assert.assertEquals(version, versions.iterator().next());

        final Set<Gap> gaps = gapService.getGaps();
        Assert.assertNotNull(gaps);
        Assert.assertEquals(1, gaps.size());
        Assert.assertEquals(gap, gaps.iterator().next());

        Set<Gap> list = getByFilter("CHTS", null, null, null);
        Assert.assertEquals(1, list.size());

        list = getByFilter("CHTS", null, null, version);
        Assert.assertEquals(1, list.size());

        list = getByFilter("CHTS", -10, 10, version);
        Assert.assertEquals(1, list.size());

        final Activity activity = MemDomainUtil.newActivity(gap);
        activity.setUser("RCTC");
        gap.add(activity);
        gapService.add(gap);

        list = getByFilter("CHTS", null, null, null);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(gap, list.iterator().next());
        Assert.assertTrue(list.iterator().next().isEmpty());

        list = getByFilter("CHTS", null, null, version);
        Assert.assertEquals(1, list.size());

        list = getByFilter("CHTS", -10, 10, version);
        Assert.assertEquals(1, list.size());
    }

    private Set<Gap> getByFilter(final String username, final Integer from, final Integer to,
        final String version) {
        final Filter filter = new Filter().byUser(username);

        if (from != null && to != null) {
            final Date fromDay = MemDomainUtil.selectDay(from);
            final Date toDay = MemDomainUtil.selectDay(to);
            filter.byDayInterval(fromDay, toDay);
        }

        if (version != null) {
            filter.byVersion(version);
        }

        final Set<Gap> gaps = gapService.getByFilter(filter);
        final Set<Activity> activities = activityService.getByFilter(filter);
        Filter.merge(gaps, activities);
        return gaps;
    }

}
