package com.hellogin.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.inject.Inject;
import com.hellogin.client.activity.ActivityFactory;
import com.hellogin.client.activity.GoodbyeActivity;
import com.hellogin.client.activity.HelloActivity;
import com.hellogin.client.place.BasePlace;
import com.hellogin.client.place.GoodbyePlace;
import com.hellogin.client.place.HelloPlace;

/**
 * GoodbyeActivityMapper
 */
public class GoodbyeActivityMapper implements ActivityMapper {
    final ActivityFactory activityFactory;

    @Inject
    public GoodbyeActivityMapper(final ActivityFactory activityFactory) {
        this.activityFactory = activityFactory;
    }

    @Override
    public Activity getActivity(final Place place) {
        GWT.log("GoodbyeActivityMapper.getActivity: " + place);

        if (place instanceof HelloPlace) {
            final GoodbyeActivity goodbye = activityFactory.goodbye((BasePlace) place);
            return goodbye;

        } else if (place instanceof GoodbyePlace) {
            final HelloActivity hello = activityFactory.hello((BasePlace) place);
            return hello;
        }

        return null;
    }

}
