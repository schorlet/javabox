package demo.hello.client.plumbing;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.gwt.place.shared.PlaceHistoryHandler;

import demo.hello.client.MainView;
import demo.hello.client.Resources;

/**
 * ProjectGinjector
 */
@GinModules({ ProjectGinModule.class })
public interface ProjectGinjector extends Ginjector {

    Resources getResources();

    PlaceHistoryHandler getPlaceHistoryHandler();

    MainView getMainView();
}
