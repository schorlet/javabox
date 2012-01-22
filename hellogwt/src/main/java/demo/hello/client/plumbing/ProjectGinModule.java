package demo.hello.client.plumbing;

import com.google.gwt.core.client.GWT;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import demo.hello.client.MainView;
import demo.hello.client.Messages;
import demo.hello.client.Resources;
import demo.hello.client.activity.CellActivityFactory;
import demo.hello.client.activity.CenterActivityMapper;
import demo.hello.client.activity.EastActivityMapper;
import demo.hello.client.activity.SouthActivityMapper;
import demo.hello.client.activity.WestActivityMapper;
import demo.hello.client.cell.CellClient;
import demo.hello.client.cell.CellDtoEditor;
import demo.hello.client.cell.CellDtoEditorView;
import demo.hello.client.cell.CellEventHub;
import demo.hello.client.cell.CellProxyEditor;
import demo.hello.client.cell.CellProxyEditorView;
import demo.hello.client.cell.DataAsyncView;
import demo.hello.client.cell.DataListView;
import demo.hello.client.cell.SelectionActivity;
import demo.hello.client.cell.SelectionView;
import demo.hello.client.cell.SimpleView;
import demo.hello.client.log.LogActivity;
import demo.hello.client.log.LogView;
import demo.hello.client.place.MainPlace;
import demo.hello.client.place.ProjectPlaceHistoryMapper;
import demo.hello.shared.ProjectRequestFactory;

/**
 * ProjectGinModule
 */
public class ProjectGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        // EventBus
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);

        // resources
        bind(Resources.class).in(Singleton.class);
        // messages
        bind(Messages.class).in(Singleton.class);

        configureCell();
        configureLog();
        configureProject();
    }

    void configureProject() {
        // MainView
        bind(MainView.class).in(Singleton.class);
        bind(MainView.ViewBinder.class).in(Singleton.class);

        /*
         * Activity mappers: finds the activity to run for a given place
         */

        // depends on CellActivityFactory
        bind(CenterActivityMapper.class).in(Singleton.class);
        // depends on LogActivity
        bind(SouthActivityMapper.class).in(Singleton.class);
        // depends on SelectionActivity
        bind(WestActivityMapper.class).in(Singleton.class);
        // depends on CellEditorActivity
        bind(EastActivityMapper.class).in(Singleton.class);

        /*
         * Place mapper: finds places to/from tokens
         * Used to configure a PlaceHistoryHandler
         */

        // PlaceHistoryMapper (needed by PlaceHistoryHandler)
        bind(PlaceHistoryMapper.class).to(ProjectPlaceHistoryMapper.class).in(Singleton.class);

        /*
         * Provided
         */

        // ProjectRequestFactory
        // PlaceController
        // PlaceHistoryHandler
    }

    void configureLog() {
        bind(LogView.class).in(Singleton.class);
        bind(LogActivity.class);
    }

    void configureCell() {
        // CellEventHub
        bind(CellEventHub.class).in(Singleton.class);

        /*
         * Views
         */

        // depends on Resources
        bind(SimpleView.class).in(Singleton.class);
        bind(DataListView.class).in(Singleton.class);
        bind(DataAsyncView.class).in(Singleton.class);
        bind(SelectionView.class).in(Singleton.class);
        bind(SelectionView.ViewBinder.class).in(Singleton.class);

        bind(CellDtoEditorView.class).in(Singleton.class);
        bind(CellDtoEditorView.ViewBinder.class).in(Singleton.class);
        bind(CellDtoEditorView.EditorDriver.class).in(Singleton.class);
        bind(CellDtoEditor.class).in(Singleton.class);

        bind(CellProxyEditorView.class).in(Singleton.class);
        bind(CellProxyEditorView.ViewBinder.class).in(Singleton.class);
        bind(CellProxyEditorView.EditorDriver.class).in(Singleton.class);
        bind(CellProxyEditor.class).in(Singleton.class);

        /*
         * Activities 
         */

        // CellActivityFactory:
        // - SimpleActivity,DataListActivity,DataAsyncActivity,
        // - CellDtoEditorActivity,CellProxyEditorActivity
        install(new GinFactoryModuleBuilder().build(CellActivityFactory.class));
        bind(SelectionActivity.class);

        /*
         * Client
         */

        // depends on ProjectRequestFactory
        bind(CellClient.class).in(Singleton.class);

    }

    /**
    * RequestFactory service endpoints.
    */
    @Provides
    @Singleton
    public ProjectRequestFactory createProjectRequestFactory(final EventBus eventBus) {
        final ProjectRequestFactory factory = GWT.create(ProjectRequestFactory.class);
        factory.initialize(eventBus);
        return factory;
    }

    /**
    * In charge of the user's location in the app.
    */
    @Provides
    @Singleton
    public PlaceController getPlaceController(final EventBus eventBus) {
        return new PlaceController(eventBus);
    }

    /**
    * Monitors {@link PlaceChangeEvent}s and
    * {@link com.google.gwt.user.client.History} events and keep them in sync.
    */
    @Provides
    @Singleton
    public PlaceHistoryHandler getPlaceHistoryHandler(final PlaceController placeController,
        final PlaceHistoryMapper historyMapper, final EventBus eventBus) {

        final PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);
        historyHandler.register(placeController, eventBus, new MainPlace());

        return historyHandler;
    }

}
