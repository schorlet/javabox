package demo.hello.client.cell;

import java.util.logging.Level;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import demo.hello.client.Logger;
import demo.hello.client.activity.BaseActivity;
import demo.hello.client.place.BasePlace;

/**
 * SimpleActivity
 */
public class SimpleActivity extends BaseActivity {
    final SimpleView simpleView;

    @Inject
    SimpleActivity(final SimpleView simpleView) {
        this.simpleView = simpleView;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget, final EventBus eventBus) {
        Logger.logp(Level.INFO, this.toString(), "start");
        containerWidget.setWidget(simpleView == null ? null : simpleView.asWidget());
    }

    @Override
    public void setPlace(final BasePlace place) {}
}
