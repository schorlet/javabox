package demo.hello.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

import demo.hello.client.cell.SimpleActivity;
import demo.hello.client.log.LogActivity;
import demo.hello.client.plumbing.SimpleGinjector;

public class SimpleModule implements EntryPoint {

    static final SimpleGinjector ginjector = GWT.create(SimpleGinjector.class);
    Resources resources;

    public void onModuleLoad() {
        resources = ginjector.getResources();
        resources.style().ensureInjected();

        final SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
        final SimpleLayoutPanel southPanel = new SimpleLayoutPanel();

        // layoutPanel (southPanel + centerPanel)
        final SplitLayoutPanel layoutPanel = new SplitLayoutPanel();
        layoutPanel.setSize("100%", "100%");
        layoutPanel.addSouth(southPanel, 100);
        layoutPanel.add(centerPanel);

        RootLayoutPanel.get().add(layoutPanel);

        // start activities
        final SimpleEventBus eventBus = new SimpleEventBus();

        final SimpleActivity simpleActivity = ginjector.getSimpleActivity();
        simpleActivity.start(centerPanel, eventBus);

        final LogActivity logActivity = ginjector.getLogActivity();
        logActivity.start(southPanel, eventBus);
    }

}
