package demo.hello.client.activity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.web.bindery.event.shared.HandlerRegistration;

import demo.hello.client.Logger;
import demo.hello.client.place.BasePlace;

/**
 * BaseActivity
 */
public abstract class BaseActivity extends AbstractActivity {
    private final Set<HandlerRegistration> registrations = new HashSet<HandlerRegistration>();

    @Override
    public void onStop() {
        Logger.logp(Level.INFO, this.toString(), "onStop");

        final Iterator<HandlerRegistration> iterator = registrations.iterator();
        while (iterator.hasNext()) {
            iterator.next().removeHandler();
            iterator.remove();
        }
    }

    protected void registerHandler(final HandlerRegistration handlerRegistration) {
        registrations.add(handlerRegistration);
    }

    public abstract void setPlace(final BasePlace place);
}
