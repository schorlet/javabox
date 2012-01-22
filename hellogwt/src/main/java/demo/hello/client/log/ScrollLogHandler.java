package demo.hello.client.log;

import java.util.logging.LogRecord;

import com.google.gwt.logging.client.HasWidgetsLogHandler;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * ScrollLogHandler
 */
public class ScrollLogHandler extends HasWidgetsLogHandler {
    final ScrollPanel scrollPanel;

    public ScrollLogHandler(final VerticalPanel logPanel, final ScrollPanel scrollPanel) {
        super(logPanel);

        logPanel.clear();
        this.scrollPanel = scrollPanel;
    }

    @Override
    public void publish(final LogRecord record) {
        if (!isLoggable(record)) return;
        super.publish(record);

        scrollPanel.scrollToBottom();
    }
}
