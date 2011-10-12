package com.hellogin.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.hellogin.client.activity.CenterActivityMapper;
import com.hellogin.client.activity.SouthActivityMapper;

/**
 * MainView
 */
public class MainView extends Composite {
    private static MainViewUiBinder uiBinder = GWT.create(MainViewUiBinder.class);

    @UiTemplate("MainView.ui.xml")
    interface MainViewUiBinder extends UiBinder<Widget, MainView> {}

    @UiField
    SimplePanel centerPanel;

    @UiField
    SimplePanel southPanel;

    @Inject
    public MainView(final CenterActivityMapper centerActivityMapper,
        final SouthActivityMapper southActivityMapper, final EventBus eventBus) {

        initWidget(uiBinder.createAndBindUi(this));

        final ActivityManager centerActivityManager = new ActivityManager(centerActivityMapper,
            eventBus);
        centerActivityManager.setDisplay(centerPanel);

        final ActivityManager southActivityManager = new ActivityManager(southActivityMapper,
            eventBus);
        southActivityManager.setDisplay(southPanel);

    }

}
