package demo.hello.client.activity;

import java.util.logging.Level;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;

import demo.hello.client.Logger;
import demo.hello.client.place.BasePlace;
import demo.hello.client.place.PlaceOne;
import demo.hello.client.place.PlaceThree;
import demo.hello.client.place.PlaceTwo;

/**
 * CenterActivityMapper.
 *
 * Finds the activity to run for a given {@link Place}, used to configure
 * an {@link ActivityManager}.
 */
public class CenterActivityMapper implements ActivityMapper {
    final CellActivityFactory cellActivityFactory;

    @Inject
    public CenterActivityMapper(final CellActivityFactory cellActivityFactory) {
        this.cellActivityFactory = cellActivityFactory;
    }

    private BaseActivity lastActivity = null;

    @Override
    public Activity getActivity(final Place place) {
        BaseActivity activity = null;
        final boolean containsKey = ((BasePlace) place).getParameters().containsKey("cellid");

        if (place instanceof PlaceOne) {
            activity = cellActivityFactory.simpleActivity();
            activity.setPlace((BasePlace) place);

        } else if (containsKey && lastActivity != null) {
            activity = lastActivity;

        } else if (place instanceof PlaceTwo) {
            activity = cellActivityFactory.dataListActivity();
            activity.setPlace((BasePlace) place);
            lastActivity = activity;

        } else if (place instanceof PlaceThree) {
            activity = cellActivityFactory.dataAsyncActivity();
            activity.setPlace((BasePlace) place);
            lastActivity = activity;
        }

        Logger.logpf(Level.INFO, "CenterActivityMapper", "getActivity", "place: %s, activity: %s",
            place, activity);
        return activity;
    }
}
