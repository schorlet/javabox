package demo.gap.server.jpa;

import org.junit.runner.RunWith;

import com.google.inject.Inject;

import demo.gap.shared.domain.DomainServiceTest;
import demo.gap.shared.domain.service.ActivityService;
import demo.gap.shared.domain.service.GapService;
import demo.gap.shared.domain.service.UserService;
import demo.gap.shared.mem.GuiceJUnit4Runner.GuiceModules;

/**
 * JpaServiceTest
 */
@RunWith(GuiceJpaJUnit4Runner.class)
@GuiceModules(GuiceJpaModule.class)
public class JpaServiceTest extends DomainServiceTest {

    @Inject
    public JpaServiceTest(final GapService gapService, final ActivityService activityRead,
        final UserService userService) {
        super(gapService, activityRead, userService);
    }

}
