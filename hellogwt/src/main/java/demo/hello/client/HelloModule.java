package demo.hello.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import demo.hello.client.plumbing.ProjectGinjector;

public class HelloModule implements EntryPoint {

    static final ProjectGinjector ginjector = GWT.create(ProjectGinjector.class);

    public void onModuleLoad() {
        final Resources resources = ginjector.getResources();
        resources.style().ensureInjected();

        final MainView mainView = ginjector.getMainView();
        RootLayoutPanel.get().add(mainView);

        final PlaceHistoryHandler historyHandler = ginjector.getPlaceHistoryHandler();
        historyHandler.handleCurrentHistory();
    }
}
