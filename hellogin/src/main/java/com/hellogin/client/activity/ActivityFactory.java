package com.hellogin.client.activity;

import com.hellogin.client.place.BasePlace;

/**
 * ActivityFactory
 */
public interface ActivityFactory {

    HelloActivity hello(final BasePlace place);

    GoodbyeActivity goodbye(final BasePlace place);

}
