package demo.hello.client.log;

import java.util.logging.Level;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import demo.hello.client.Logger;
import demo.hello.client.Resources;

/**
 * LogView
 */
public class LogView extends ResizeComposite implements KeyPressHandler, ClickHandler {
    final VerticalPanel logPanel = new VerticalPanel();
    final ScrollPanel scrollPanel;

    @Inject
    LogView(final Resources resources) {
        final FocusPanel focusPanel = new FocusPanel(logPanel);
        focusPanel.addKeyPressHandler(this);

        scrollPanel = new ScrollPanel(focusPanel);
        scrollPanel.setSize("100%", "100%");

        final Widget imageWidget = imageWidget(resources, "image_clear_all");
        // DOM.setStyleAttribute(imageWidget.getElement(), "zIndex", "99");

        final Widget imageWidget2 = imageWidget(resources, "image_clear_one");
        // DOM.setStyleAttribute(imageWidget2.getElement(), "zIndex", "99");

        final Widget imageWidget3 = imageWidget(resources, "image_copy");
        // DOM.setStyleAttribute(imageWidget3.getElement(), "zIndex", "99");

        final LayoutPanel layoutPanel = new LayoutPanel();
        layoutPanel.setSize("100%", "100%");
        layoutPanel.add(imageWidget);
        layoutPanel.add(imageWidget2);
        layoutPanel.add(imageWidget3);
        layoutPanel.add(scrollPanel);

        layoutPanel.setWidgetTopHeight(imageWidget, 1, Unit.PX, 19, Unit.PX);
        layoutPanel.setWidgetRightWidth(imageWidget, 20, Unit.PX, 19, Unit.PX);

        layoutPanel.setWidgetTopHeight(imageWidget2, 1, Unit.PX, 19, Unit.PX);
        layoutPanel.setWidgetRightWidth(imageWidget2, 40, Unit.PX, 19, Unit.PX);

        layoutPanel.setWidgetTopHeight(imageWidget3, 1, Unit.PX, 19, Unit.PX);
        layoutPanel.setWidgetRightWidth(imageWidget3, 60, Unit.PX, 19, Unit.PX);

        initWidget(layoutPanel);
    }

    public VerticalPanel getLogPanel() {
        return logPanel;
    }

    public ScrollPanel getScrollPanel() {
        return scrollPanel;
    }

    Widget imageWidget(final Resources resources, final String name) {
        final Image image = new Image(resources.cross());

        final Anchor anchor = new Anchor();
        anchor.sinkEvents(Event.ONCLICK);
        anchor.addHandler(this, ClickEvent.getType());
        anchor.setName(name);

        DOM.insertBefore(anchor.getElement(), image.getElement(),
            DOM.getFirstChild(anchor.getElement()));

        final SimplePanel panel = new SimplePanel(anchor);
        panel.setStylePrimaryName("imgButton");

        return panel;
    }

    @Override
    public void onKeyPress(final KeyPressEvent event) {
        if (event.isControlKeyDown() && event.getCharCode() == 'x') {
            logPanel.clear();
            Logger.logp(Level.INFO, "LogView", "onKeyPress", "Ctrl+X");
        }
    }

    @Override
    public void onClick(final ClickEvent event) {
        final Object source = event.getSource();

        if (source instanceof Anchor) {
            final String name = ((Anchor) source).getName();

            if ("image_clear_all".equals(name)) {
                logPanel.clear();

            } else if ("image_clear_one".equals(name)) {
                if (logPanel.getWidgetCount() > 0) {
                    logPanel.remove(0);
                }

            } else if ("image_copy".equals(name)) {
                final int count = logPanel.getWidgetCount();
                final StringBuilder sb = new StringBuilder();

                for (int i = 0; i < count; i++) {
                    sb.append(logPanel.getWidget(i).getElement().getInnerText()).append('\n');
                }

                if (sb.length() > 0) {
                    Window.alert(sb.toString());
                }
            }
        }
    }
}
