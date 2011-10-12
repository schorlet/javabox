package com.hellogin.client.gin;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.hellogin.client.MainView;
import com.hellogin.client.activity.ActivityFactory;
import com.hellogin.client.activity.GoodbyeActivityMapper;
import com.hellogin.client.activity.HelloActivityMapper;
import com.hellogin.client.place.AppPlaceHistoryMapper;
import com.hellogin.client.place.PlaceOne;
import com.hellogin.client.view.GoodbyeView;
import com.hellogin.client.view.HelloView;

/**
 * HelloGinModule
 */
public class HelloGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);

        bind(PlaceHistoryMapper.class).to(AppPlaceHistoryMapper.class).in(Singleton.class);

        bind(HelloActivityMapper.class).in(Singleton.class);
        bind(GoodbyeActivityMapper.class).in(Singleton.class);

        bind(GoodbyeView.class).in(Singleton.class);
        bind(HelloView.class).in(Singleton.class);
        bind(MainView.class).in(Singleton.class);

        // binds @AssistedInject(ed) HelloActivity and GoodbyeActivity via ActivityFactory
        install(new GinFactoryModuleBuilder().build(ActivityFactory.class));
    }

    @Provides
    @Singleton
    public PlaceController getPlaceController(final EventBus eventBus) {
        return new PlaceController(eventBus);
    }

    @Provides
    @Singleton
    public PlaceHistoryHandler getHistoryHandler(final PlaceController placeController,
        final PlaceHistoryMapper historyMapper, final EventBus eventBus) {

        final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, eventBus, new PlaceOne("GIN!"));
        return historyHandler;
    }

}
