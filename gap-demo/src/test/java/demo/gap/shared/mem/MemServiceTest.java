package demo.gap.shared.mem;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

import demo.gap.shared.domain.DomainServiceTest;
import demo.gap.shared.domain.pojo.Activity;
import demo.gap.shared.domain.pojo.Gap;
import demo.gap.shared.domain.service.ActivityService;
import demo.gap.shared.domain.service.Filter;
import demo.gap.shared.domain.service.GapService;
import demo.gap.shared.domain.service.UserService;
import demo.gap.shared.mem.GuiceJUnit4Runner.GuiceModules;

/**
 * MemServiceTest
 */
@RunWith(GuiceJUnit4Runner.class)
@GuiceModules(GuiceMemModule.class)
public class MemServiceTest extends DomainServiceTest {

    @Inject
    public MemServiceTest(final GapService gapService, final ActivityService activityRead,
        final UserService userService) {
        super(gapService, activityRead, userService);
    }

    @Test
    public void testFilter() {
        // newActivity
        final Activity newActivity = MemDomainUtil.randomActivity();
        filterActivity(false, newActivity, newActivity);

        // newActivity2
        final Activity newActivity2 = MemDomainUtil.randomActivity();
        newActivity2.setDay(MemDomainUtil.nextDay(newActivity.getDay(), 10));
        newActivity2.setUser(newActivity.getUser().replaceFirst("\\w", "Z"));
        newActivity2.getGap().setVersion(newActivity.getVersion().replaceFirst("\\d", "2"));

        filterActivity(true, newActivity, newActivity2);

        // newGap
        final Gap newGap = MemDomainUtil.newGap(newActivity);
        filterGap(false, newGap, newGap);

        // newGap2
        final Gap newGap2 = MemDomainUtil.newGap(newActivity2);
        filterGap(true, newGap, newGap2);

        // newGap3
        final Gap newGap3 = MemDomainUtil.newGap(newActivity);
        filterGap(false, newGap, newGap3);

        // newActivity
        final Activity newActivity3 = MemDomainUtil.randomActivity(newGap3);
        newGap3.add(newActivity3);
        filterGap(false, newGap, newGap3);

    }

    void filterActivity(final boolean xor, final Activity activity, final Activity activity2) {
        final Date fromDay = MemDomainUtil.nextDay(activity.getDay(), -1);
        final Date toDay = MemDomainUtil.nextDay(activity.getDay(), 1);

        Assert.assertTrue(xor ^ new Filter().byId(activity.getId()).apply(activity2));
        Assert.assertTrue(xor ^ new Filter().byUser(activity.getUser()).apply(activity2));
        Assert.assertTrue(xor ^ new Filter().byVersion(activity.getVersion()).apply(activity2));
        Assert.assertTrue(xor ^ new Filter().byDay(activity.getDay()).apply(activity2));
        Assert.assertTrue(xor ^ new Filter().byDayInterval(fromDay, toDay).apply(activity2));

        final Filter filter = new Filter().byUser(activity.getUser());
        Assert.assertTrue(xor ^ filter.apply(activity2));

        filter.byVersion(activity.getVersion());
        Assert.assertTrue(xor ^ filter.apply(activity2));

        filter.byDay(activity.getDay());
        Assert.assertTrue(xor ^ filter.apply(activity2));

        filter.byDayInterval(fromDay, toDay);
        Assert.assertTrue(xor ^ filter.apply(activity2));
    }

    void filterGap(final boolean xor, final Gap gap, final Gap gap2) {
        final Activity activity = gap.getActivities().iterator().next();
        final Date fromDay = MemDomainUtil.nextDay(activity.getDay(), -1);
        final Date toDay = MemDomainUtil.nextDay(activity.getDay(), 1);

        Assert.assertTrue(xor ^ new Filter().byGapId(gap.getId()).apply(MemDomainUtil.copy(gap2)));
        Assert.assertTrue(xor
            ^ new Filter().byUser(activity.getUser()).apply(MemDomainUtil.copy(gap2)));
        Assert.assertTrue(xor
            ^ new Filter().byVersion(gap.getVersion()).apply(MemDomainUtil.copy(gap2)));
        Assert.assertTrue(xor
            ^ new Filter().byDay(activity.getDay()).apply(MemDomainUtil.copy(gap2)));
        Assert.assertTrue(xor
            ^ new Filter().byDayInterval(fromDay, toDay).apply(MemDomainUtil.copy(gap2)));

        final Filter filter2 = new Filter().byUser(activity.getUser());
        Assert.assertTrue(xor ^ filter2.apply(MemDomainUtil.copy(gap2)));

        filter2.byVersion(gap.getVersion());
        Assert.assertTrue(xor ^ filter2.apply(MemDomainUtil.copy(gap2)));

        filter2.byDay(activity.getDay());
        Assert.assertTrue(xor ^ filter2.apply(MemDomainUtil.copy(gap2)));

        filter2.byDayInterval(fromDay, toDay);
        Assert.assertTrue(xor ^ filter2.apply(MemDomainUtil.copy(gap2)));

        if (!xor) {
            new Filter().byDayInterval(fromDay, toDay).apply(gap2);
            Assert.assertEquals(1, gap2.getActivities().size());
            Assert.assertTrue(gap2.getActivities().contains(activity));
        }
    }

}
