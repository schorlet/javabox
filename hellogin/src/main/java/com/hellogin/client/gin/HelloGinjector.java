package com.hellogin.client.gin;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.hellogin.client.MainView;

/**
 * HelloGinjector
 */
@GinModules({ HelloGinModule.class })
public interface HelloGinjector extends Ginjector {

    PlaceHistoryHandler getPlaceHistoryHandler();

    MainView getMainView();
}
