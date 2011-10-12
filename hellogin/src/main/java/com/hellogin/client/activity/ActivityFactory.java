package com.hellogin.client.activity;

import com.hellogin.client.place.BasePlace;
import com.hellogin.client.view.GoodbyeActivity;
import com.hellogin.client.view.HelloActivity;

/**
 * ActivityFactory.
 *
 * create activities with the given place
 */
public interface ActivityFactory {

    HelloActivity helloActivity(final BasePlace place);

    GoodbyeActivity goodbyeActivity(final BasePlace place);

}
