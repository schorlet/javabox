package demo.hello.client.cell;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.shared.EventHandler;

import demo.hello.client.cell.CellFilterEvent.CellFilterHandler;

/**
 * CellFilterEvent
 */
public class CellFilterEvent extends CellEvent<CellFilterHandler> {
    /**
     * CellFilterHandler
     */
    public interface CellFilterHandler extends EventHandler {
        void onFilterCell(CellFilterEvent event);
    }

    public CellFilterEvent(final String c) {
        this.a = null;
        this.c = c;
        this.regex = false;
    }

    public CellFilterEvent(final String a, final boolean regex) {
        this.a = a;
        this.c = null;
        this.regex = regex;
    }

    private final boolean regex;
    private final String a;
    private final String c;

    public String getA() {
        return a;
    }

    public String getC() {
        return c;
    }

    public boolean isRegex() {
        return regex;
    }

    public static Type<CellFilterHandler> TYPE = new Type<CellFilterHandler>();

    @Override
    public Type<CellFilterHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final CellFilterHandler handler) {
        handler.onFilterCell(this);
    }

    @Override
    public Map<String, String> getParameters() {
        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("a", a);
        parameters.put("c", c);
        parameters.put("re", Boolean.toString(regex));
        return parameters;
    }
}
