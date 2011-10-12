package com.hellogin.client.view;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.hellogin.client.place.BasePlace;
import com.hellogin.client.place.PlaceOne;
import com.hellogin.client.place.PlaceTwo;

/**
 * HelloActivity
 */
public class HelloActivity extends AbstractActivity {
    final HelloView helloView;
    final PlaceController placeController;
    final BasePlace place;

    @AssistedInject
    public HelloActivity(final HelloView helloView, final PlaceController placeController,
        @Assisted final BasePlace place) {
        this.helloView = helloView;
        this.placeController = placeController;
        this.place = place;
    }

    HandlerRegistration goodByeHandler = null;

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        helloView.setName(place.getName());

        goodByeHandler = helloView.goodBye().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                if (place instanceof PlaceOne) {
                    placeController.goTo(new PlaceTwo(place.getName()));

                } else if (place instanceof PlaceTwo) {
                    placeController.goTo(new PlaceOne(place.getName()));
                }
            }
        });

        containerWidget.setWidget(helloView.asWidget());
    }

    @Override
    public void onStop() {
        if (goodByeHandler != null) {
            goodByeHandler.removeHandler();
        }
    }
}
