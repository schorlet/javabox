package demo.hello.client.plumbing;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;
import com.google.web.bindery.event.shared.EventBus;

import demo.hello.client.Resources;
import demo.hello.client.cell.SimpleActivity;
import demo.hello.client.log.LogActivity;

/**
 * SimpleGinjector
 */
@GinModules({ SimpleGinModule.class })
public interface SimpleGinjector extends Ginjector {

    Resources getResources();

    LogActivity getLogActivity();

    SimpleActivity getSimpleActivity();

    EventBus getEventBus();
}
