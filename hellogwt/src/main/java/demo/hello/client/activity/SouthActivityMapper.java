package demo.hello.client.activity;

import java.util.logging.Level;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.google.inject.Provider;

import demo.hello.client.Logger;
import demo.hello.client.log.LogActivity;

/**
 * SouthActivityMapper
 */
public class SouthActivityMapper implements ActivityMapper {
    final Provider<LogActivity> logActivityProvider;
    LogActivity logActivity = null;

    @Inject
    public SouthActivityMapper(final Provider<LogActivity> logActivityProvider) {
        this.logActivityProvider = logActivityProvider;
    }

    @Override
    public Activity getActivity(final Place place) {
        if (logActivity == null) {
            logActivity = logActivityProvider.get();
        }

        Logger.logpf(Level.FINE, "SouthActivityMapper", "getActivity", "place: %s, activity: %s",
            place, logActivity);

        return logActivity;
    }
}
