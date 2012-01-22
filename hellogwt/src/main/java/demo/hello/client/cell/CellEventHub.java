package demo.hello.client.cell;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.place.shared.PlaceController;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import demo.hello.client.Logger;
import demo.hello.client.place.BasePlace;

/**
 * CellEventHub
 */
public class CellEventHub {
    final EventBus eventBus;
    final PlaceController placeController;

    boolean history = true;

    public boolean isHistory() {
        return history;
    }

    @Inject
    CellEventHub(final EventBus eventBus, final PlaceController placeController) {
        this.eventBus = eventBus;
        this.placeController = placeController;
    }

    /**
     * fireFromSource
     */
    public void fireFromSource(final CellEvent<?> event, final CellEventSource source) {
        if (isHistory()) {
            Logger.logp(Level.FINE, "CellEventHub", "fireFromSource to history", source);
            goToSamePlace(event.getParameters());

        } else {
            Logger.logp(Level.FINE, "CellEventHub", "fireFromSource to eventBus", source);
            eventBus.fireEventFromSource(event, source);
        }
    }

    /**
     * addHandlerToSource
     */
    public <H> HandlerRegistration addHandlerToSource(final Type<H> type,
        final CellEventSource source, final H handler) {

        // if (isHistory()) return FAKE_REGISTRATION;
        return eventBus.addHandlerToSource(type, source, handler);
    }

    // private static final HandlerRegistration FAKE_REGISTRATION = new HandlerRegistration() {
    // public void removeHandler() {}
    // };

    /**
     * goToSamePlace
     */
    private void goToSamePlace(final Map<String, String> params) {
        final BasePlace basePlace = (BasePlace) placeController.getWhere();

        // get place params
        final Map<String, String> parameters = basePlace.getParameters();
        history = Boolean.valueOf(parameters.get("history"));

        // merge place params with event params
        parameters.putAll(params);

        // remove some entries
        final Iterator<Entry<String, String>> iterator = parameters.entrySet().iterator();
        while (iterator.hasNext()) {
            final Entry<String, String> entry = iterator.next();
            if (removeKeys.contains(entry.getKey())) {
                iterator.remove();
            } else if (null == entry.getValue() || "null".equals(entry.getValue())) {
                iterator.remove();
            }
        }

        removeKeys.clear();
        placeController.goTo(basePlace.newPlace(parameters));
    }

    private final Set<String> removeKeys = new HashSet<String>();

    /**
     * removeOnNextEvent
     */
    public void removeOnNextEvent(final String key) {
        if (isHistory()) {
            removeKeys.add(key);
        }
    }
}

/**
 * CellEventSource
 */
enum CellEventSource {
    DATA_LIST_VIEW, //
    DATA_ASYNC_VIEW, //
    SELECTION_ACTIVITY, //
    CELLDTO_EDITOR_VIEW, //
    CELLPROXY_EDITOR_VIEW;
}
