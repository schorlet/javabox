package demo.hello.client.plumbing;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import demo.hello.client.Resources;
import demo.hello.client.cell.SimpleActivity;
import demo.hello.client.cell.SimpleView;
import demo.hello.client.log.LogActivity;
import demo.hello.client.log.LogView;

/**
 * SimpleGinModule
 */
public class SimpleGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        // EventBus
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);

        // resources
        bind(Resources.class).in(Singleton.class);
        // messages
        // bind(Messages.class).in(Singleton.class);

        bind(LogView.class).in(Singleton.class);
        bind(LogActivity.class);

        bind(SimpleView.class).in(Singleton.class);
        bind(SimpleActivity.class);

    }

}
