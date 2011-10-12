package com.hellogin.client.activity;

import com.hellogin.client.place.BasePlace;
import com.hellogin.client.view.GoodbyeActivity;
import com.hellogin.client.view.HelloActivity;

/**
 * ActivityFactory
 */
public interface ActivityFactory {

    HelloActivity hello(final BasePlace place);

    GoodbyeActivity goodbye(final BasePlace place);

}
