package demo.gap.shared.mem;

import java.util.Set;

import demo.gap.shared.domain.pojo.Gap;
import demo.gap.shared.domain.service.ActivityService;
import demo.gap.shared.domain.service.GapService;
import demo.gap.shared.domain.service.UserService;

/**
 * SampleData
 */
public class SampleData {

    public static void reset(final GapService gapService, final ActivityService activityService,
        final UserService userService) {

        gapService.clear();
        userService.clear();

        userService.addAll(MemDomainUtil.getUsers());

        for (int i = 0; i < 8; i++) {
            final Set<Gap> gaps = MemDomainUtil.newGapsWithActivities();
            gapService.addAll(gaps);
            gapService.addAll(MemDomainUtil.newGaps());

            for (final Gap gap : gaps) {
                activityService.addAll(gap.getActivities());
            }
        }
    }
}
