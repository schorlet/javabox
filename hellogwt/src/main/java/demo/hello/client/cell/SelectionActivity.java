package demo.hello.client.cell;

import java.util.Map;
import java.util.logging.Level;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import demo.hello.client.Logger;
import demo.hello.client.activity.BaseActivity;
import demo.hello.client.place.BasePlace;
import demo.hello.client.place.PlaceTwo;

/**
 * SelectionActivity
 */
public class SelectionActivity extends BaseActivity {
    final SelectionView selectionView;
    final CellEventHub eventHub;

    @Inject
    SelectionActivity(final CellEventHub eventHub, final SelectionView selectionView) {
        this.eventHub = eventHub;
        this.selectionView = selectionView;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget,
        final com.google.gwt.event.shared.EventBus eventBus) {

        Logger.logp(Level.INFO, this.toString(), "start");

        if (selectionView == null) {
            containerWidget.setWidget(null);

        } else {
            containerWidget.setWidget(selectionView.asWidget());
            startActivity();
        }
    }

    @Override
    public void setPlace(final BasePlace place) {
        final Map<String, String> parameters = place.getParameters();
        if (parameters.containsKey("a")) {
            selectionView.setA(parameters.get("a"));
        }
        selectionView.regexEnabled(place instanceof PlaceTwo);
    }

    void startActivity() {
        registerEventsHandlers();
    }

    /**
     * register events handlers
     */
    void registerEventsHandlers() {
        registerHandler(selectionView.filter().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                eventHub.fireFromSource(new CellFilterEvent(selectionView.getA(), false),
                    CellEventSource.SELECTION_ACTIVITY);
            }
        }));

        registerHandler(selectionView.regex().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                eventHub.fireFromSource(new CellFilterEvent(selectionView.getA(), true),
                    CellEventSource.SELECTION_ACTIVITY);
            }
        }));

        registerHandler(selectionView.clear().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                eventHub.fireFromSource(new CellFilterEvent(null, false),
                    CellEventSource.SELECTION_ACTIVITY);
            }
        }));
    }
}
