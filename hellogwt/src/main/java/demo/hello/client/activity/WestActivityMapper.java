package demo.hello.client.activity;

import java.util.logging.Level;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;

import demo.hello.client.Logger;
import demo.hello.client.cell.SelectionActivity;
import demo.hello.client.place.BasePlace;
import demo.hello.client.place.PlaceThree;
import demo.hello.client.place.PlaceTwo;

/**
 * WestActivityMapper
 */
public class WestActivityMapper implements ActivityMapper {
    final SelectionActivity selectionActivity;

    @Inject
    public WestActivityMapper(final SelectionActivity selectionActivity) {
        this.selectionActivity = selectionActivity;
    }

    @Override
    public Activity getActivity(final Place place) {
        Activity activity = null;

        if (place instanceof PlaceTwo || place instanceof PlaceThree) {
            selectionActivity.setPlace((BasePlace) place);
            activity = selectionActivity;
        }

        Logger.logpf(Level.FINE, "WestActivityMapper", "getActivity", "place: %s, activity: %s",
            place, activity);

        return activity;
    }
}
