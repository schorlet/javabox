package com.hellogin.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.hellogin.client.gin.HelloGinjector;

/**
 * HelloGIN
 */
public class HelloGIN implements EntryPoint {
    static final HelloGinjector ginjector = GWT.create(HelloGinjector.class);

    public void onModuleLoad() {
        final MainView mainView = ginjector.getMainView();
        RootLayoutPanel.get().add(mainView);

        final PlaceHistoryHandler historyHandler = ginjector.getPlaceHistoryHandler();
        historyHandler.handleCurrentHistory();
    }
}
