package demo.hello.client.cell;

import java.util.logging.Level;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import demo.hello.client.Logger;
import demo.hello.client.activity.BaseActivity;
import demo.hello.client.cell.CellEditEvent.CellEditHandler;
import demo.hello.client.place.BasePlace;
import demo.hello.shared.ProjectRequestFactory.CellRequest;
import demo.hello.shared.cell.CellProxy;

/**
 * CellProxyEditorActivity
 */
public class CellProxyEditorActivity extends BaseActivity {
    final CellEventHub eventHub;
    final CellProxyEditorView view;
    final CellClient cellClient;

    @Inject
    CellProxyEditorActivity(final CellEventHub eventHub, final CellProxyEditorView view,
        final CellClient cellClient) {
        this.eventHub = eventHub;
        this.view = view;
        this.cellClient = cellClient;
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

    private Integer cellid = null;

    @Override
    public void setPlace(final BasePlace place) {
        try {
            cellid = Integer.valueOf(place.getParameters().get("cellid"));
        } catch (final Exception e) {}
    }

    void startActivity() {
        registerEventsHandlers();

        cellClient.findCell(cellid, new Receiver<CellProxy>() {
            @Override
            public void onSuccess(final CellProxy cellProxy) {
                edit(cellProxy);
            }

            @Override
            public void onFailure(final ServerFailure error) {
                Logger.logp(Level.WARNING, "CellProxyEditorActivity.startActivity()", "onFailure",
                    error.getMessage(), error.getStackTraceString());

                eventHub.removeOnNextEvent("cellid");
                edit(null);
            }
        });
    }

    void registerEventsHandlers() {
        // cellEditHandler
        registerHandler(eventHub.addHandlerToSource(CellEditEvent.TYPE,
            CellEventSource.DATA_ASYNC_VIEW, new CellEditHandler() {
                @Override
                public void onEditCell(final CellEditEvent event) {
                    edit(event.getCellProxy());
                }
            }));
    }

    private void edit(final CellProxy cellProxy) {
        final CellRequest newCellRequest = cellClient.newCellRequest();
        newCellRequest.updateCell(cellProxy);
        view.edit(cellProxy, newCellRequest);
    }
}
