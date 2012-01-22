package demo.hello.client.activity;

import java.util.Map;
import java.util.logging.Level;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;

import demo.hello.client.Logger;
import demo.hello.client.place.BasePlace;
import demo.hello.client.place.PlaceThree;
import demo.hello.client.place.PlaceTwo;

/**
 * EastActivityMapper
 */
public class EastActivityMapper implements ActivityMapper {
    final CellActivityFactory cellActivityFactory;

    @Inject
    public EastActivityMapper(final CellActivityFactory cellActivityFactory) {
        this.cellActivityFactory = cellActivityFactory;
    }

    @Override
    public Activity getActivity(final Place place) {
        BaseActivity activity = null;

        final Map<String, String> parameters = ((BasePlace) place).getParameters();
        final boolean history = Boolean.valueOf(parameters.get("history"));
        final boolean getActivity = history == false ? true : parameters.containsKey("cellid");

        if (getActivity && place instanceof PlaceTwo) {
            activity = cellActivityFactory.dtoEditorActivity();
            activity.setPlace((BasePlace) place);

        } else if (getActivity && place instanceof PlaceThree) {
            activity = cellActivityFactory.proxyEditorActivity();
            activity.setPlace((BasePlace) place);
        }

        Logger.logpf(Level.FINE, "EastActivityMapper", "getActivity", "place: %s, activity: %s",
            place, activity);

        return activity;
    }
}
