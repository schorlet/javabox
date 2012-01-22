package demo.hello.client.cell;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.shared.EventHandler;

import demo.hello.client.cell.CellEditEvent.CellEditHandler;
import demo.hello.shared.cell.CellDTO;
import demo.hello.shared.cell.CellProxy;

/**
 * CellEditEvent
 */
public class CellEditEvent extends CellEvent<CellEditHandler> {
    /**
     * CellEditHandler
     */
    public interface CellEditHandler extends EventHandler {
        void onEditCell(CellEditEvent event);
    }

    public CellEditEvent() {
        this.cellDTO = null;
        this.cellProxy = null;
    }

    public CellEditEvent(final CellDTO cellDTO) {
        this.cellDTO = cellDTO;
        this.cellProxy = null;
    }

    public CellEditEvent(final CellProxy cellProxy) {
        this.cellDTO = null;
        this.cellProxy = cellProxy;
    }

    private final CellDTO cellDTO;
    private final CellProxy cellProxy;

    public CellDTO getCellDTO() {
        return cellDTO;
    }

    public CellProxy getCellProxy() {
        return cellProxy;
    }

    public static Type<CellEditHandler> TYPE = new Type<CellEditHandler>();

    @Override
    public Type<CellEditHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final CellEditHandler handler) {
        handler.onEditCell(this);
    }

    @Override
    public Map<String, String> getParameters() {
        String value = null;

        if (cellDTO != null) {
            value = cellDTO.getA();

        } else if (cellProxy != null) {
            value = String.valueOf(cellProxy.getId());
        }

        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("cellid", value);
        return parameters;
    }
}
