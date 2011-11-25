package com.hellogin.client.activity;

import java.util.logging.Level;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.hellogin.client.Logger;
import com.hellogin.client.place.BasePlace;
import com.hellogin.client.place.PlaceOne;
import com.hellogin.client.place.PlaceTwo;

/**
 * SouthActivityMapper
 */
public class SouthActivityMapper implements ActivityMapper {
    final ActivityFactory activityFactory;

    @Inject
    public SouthActivityMapper(final ActivityFactory activityFactory) {
        this.activityFactory = activityFactory;
    }

    @Override
    public Activity getActivity(final Place place) {
        AbstractActivity activity = null;

        if (place instanceof PlaceOne) {
            activity = activityFactory.goodbyeActivity((BasePlace) place);

        } else if (place instanceof PlaceTwo) {
            activity = activityFactory.helloActivity((BasePlace) place);
        }

        Logger.logp(Level.INFO, "SouthActivityMapper", "getActivity", place, activity);
        return activity;
    }

}
