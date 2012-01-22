package demo.hello.client.cell;

import java.util.logging.Level;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import demo.hello.client.Logger;
import demo.hello.client.activity.BaseActivity;
import demo.hello.client.cell.CellEditEvent.CellEditHandler;
import demo.hello.client.place.BasePlace;
import demo.hello.shared.cell.CellDTO;

/**
 * CellDtoEditorActivity
 */
public class CellDtoEditorActivity extends BaseActivity {
    final CellDtoEditorView view;
    final CellEventHub eventHub;

    @Inject
    CellDtoEditorActivity(final CellEventHub eventHub, final CellDtoEditorView view) {
        this.eventHub = eventHub;
        this.view = view;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget,
        final com.google.gwt.event.shared.EventBus eventBus) {

        Logger.logp(Level.INFO, this.toString(), "start");

        if (view == null) {
            containerWidget.setWidget(null);

        } else {
            containerWidget.setWidget(view.asWidget());
            startActivity();
        }
    }

    private String cellid = null;

    @Override
    public void setPlace(final BasePlace place) {
        cellid = place.getParameters().get("cellid");
    }

    void startActivity() {
        registerEventsHandlers();

        final CellDTO cellDTO = CellDtoProvider.getByA(cellid);
        if (cellDTO == null) {
            eventHub.removeOnNextEvent("cellid");
        }
        view.edit(cellDTO);
    }

    void registerEventsHandlers() {
        // cellEditHandler
        registerHandler(eventHub.addHandlerToSource(CellEditEvent.TYPE,
            CellEventSource.DATA_LIST_VIEW, new CellEditHandler() {
                @Override
                public void onEditCell(final CellEditEvent event) {
                    view.edit(event.getCellDTO());
                }
            }));
    }
}
