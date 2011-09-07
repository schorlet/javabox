package com.hellogin.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.hellogin.client.place.BasePlace;
import com.hellogin.client.place.GoodbyePlace;
import com.hellogin.client.place.HelloPlace;
import com.hellogin.client.ui.HelloView;

/**
 * HelloActivity
 */
public class HelloActivity extends AbstractActivity {
    final BasePlace place;
    final HelloView view;
    final PlaceController placeController;

    @AssistedInject
    public HelloActivity(final HelloView view, final PlaceController placeController,
        @Assisted final BasePlace place) {
        this.place = place;
        this.view = view;
        this.placeController = placeController;
    }

    HandlerRegistration goodByeHandler = null;

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        view.setName(place.getName());

        goodByeHandler = view.goodBye().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                if (place instanceof HelloPlace) {
                    placeController.goTo(new GoodbyePlace(place.getName()));

                } else if (place instanceof GoodbyePlace) {
                    placeController.goTo(new HelloPlace(place.getName()));
                }
            }
        });

        GWT.log("HelloActivity.startActivity: " + place);
        containerWidget.setWidget(view.asWidget());
    }

    @Override
    public void onStop() {
        GWT.log("HelloActivity.stopActivity: " + place);
        if (goodByeHandler != null) {
            goodByeHandler.removeHandler();
        }
    }
}
