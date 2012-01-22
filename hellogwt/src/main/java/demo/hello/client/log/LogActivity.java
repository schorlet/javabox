package demo.hello.client.log;

import java.util.logging.Level;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import demo.hello.client.Logger;

/**
 * LogActivity
 */
public class LogActivity extends AbstractActivity {
    final LogView logView;

    @Inject
    public LogActivity(final LogView logView) {
        this.logView = logView;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        Logger.logp(Level.INFO, "LogActivity", "start");

        if (logView == null) {
            containerWidget.setWidget(null);

        } else {
            containerWidget.setWidget(logView.asWidget());

            scrollLogHandler = new ScrollLogHandler(logView.getLogPanel(), logView.getScrollPanel());
            Logger.addHandler(scrollLogHandler);
        }
    }

    ScrollLogHandler scrollLogHandler = null;

    @Override
    public void onStop() {
        Logger.logp(Level.INFO, "LogActivity", "onStop");

        if (scrollLogHandler != null) {
            Logger.removeHandler(scrollLogHandler);
            scrollLogHandler = null;
        }
    }
}
