package com.hellogin.client.activity;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.hellogin.client.place.BasePlace;
import com.hellogin.client.place.PlaceTwo;
import com.hellogin.client.place.PlaceOne;
import com.hellogin.client.view.GoodbyeActivity;
import com.hellogin.client.view.HelloActivity;

/**
 * GoodbyeActivityMapper
 */
public class SouthActivityMapper implements ActivityMapper {
    final ActivityFactory activityFactory;

    @Inject
    public SouthActivityMapper(final ActivityFactory activityFactory) {
        this.activityFactory = activityFactory;
    }

    @Override
    public Activity getActivity(final Place place) {
        GWT.log("GoodbyeActivityMapper.getActivity: " + place);

        if (place instanceof PlaceOne) {
            final GoodbyeActivity goodbye = activityFactory.goodbye((BasePlace) place);
            return goodbye;

        } else if (place instanceof PlaceTwo) {
            final HelloActivity hello = activityFactory.hello((BasePlace) place);
            return hello;
        }

        return null;
    }

}
