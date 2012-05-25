package demo.gap.server.mongo;

import org.junit.runner.RunWith;

import com.google.inject.Inject;

import demo.gap.shared.domain.DomainServiceTest;
import demo.gap.shared.domain.service.ActivityService;
import demo.gap.shared.domain.service.GapService;
import demo.gap.shared.domain.service.UserService;
import demo.gap.shared.mem.GuiceJUnit4Runner.GuiceModules;

/**
 * MongoServiceTest
 */
@RunWith(GuiceMongoJUnit4Runner.class)
@GuiceModules(GuiceMongoModule.class)
public class MongoServiceTest extends DomainServiceTest {

    @Inject
    public MongoServiceTest(final GapService gapService, final ActivityService activityRead,
        final UserService userService) {
        super(gapService, activityRead, userService);
    }

}
