package demo.hello.client.cell;

import java.util.Map;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * CellEvent
 */
public abstract class CellEvent<H extends EventHandler> extends GwtEvent<H> {
    public abstract Map<String, String> getParameters();
}
