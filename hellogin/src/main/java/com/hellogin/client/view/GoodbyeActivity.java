package com.hellogin.client.view;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.hellogin.client.place.BasePlace;

/**
 * GoodbyeActivity
 */
public class GoodbyeActivity extends AbstractActivity {
    final BasePlace place;

    final GoodbyeView goodbyeView;

    @AssistedInject
    public GoodbyeActivity(final GoodbyeView goodbyeView, @Assisted final BasePlace place) {
        this.place = place;
        this.goodbyeView = goodbyeView;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        goodbyeView.setSuffix(place.getSuffix());
        containerWidget.setWidget(goodbyeView.asWidget());
    }
}
