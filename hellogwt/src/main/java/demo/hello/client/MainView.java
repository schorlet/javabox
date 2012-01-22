package demo.hello.client;

import java.util.Map;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import demo.hello.client.activity.CenterActivityMapper;
import demo.hello.client.activity.EastActivityMapper;
import demo.hello.client.activity.SouthActivityMapper;
import demo.hello.client.activity.WestActivityMapper;
import demo.hello.client.place.BasePlace;
import demo.hello.client.place.PlaceOne;
import demo.hello.client.place.PlaceThree;
import demo.hello.client.place.PlaceTwo;

/**
 * MainView
 */
public class MainView extends ResizeComposite {
    @UiTemplate("MainView.ui.xml")
    public interface ViewBinder extends UiBinder<Widget, MainView> {}

    @UiField(provided = true)
    SplitLayoutPanel splitLayoutPanel;

    @UiField
    TabBar tabBar;

    @UiField
    SimplePanel centerPanel;

    @UiField
    SimplePanel southPanel;

    @UiField
    SimplePanel westPanel;

    @UiField
    SimplePanel eastPanel;

    final PlaceController placeController;

    @Inject
    MainView(final ViewBinder uiBinder, final PlaceController placeController,
        final CenterActivityMapper centerActivityMapper, //
        final SouthActivityMapper southActivityMapper, //
        final WestActivityMapper westActivityMapper, //
        final EastActivityMapper eastActivityMapper, //
        final EventBus eventBus) {

        splitLayoutPanel = new SplitLayoutPanel(4);
        initWidget(uiBinder.createAndBindUi(this));

        this.placeController = placeController;

        tabBar.addTab("Cell Simple");
        tabBar.addTab("Cell Table List");
        tabBar.addTab("Cell Table Async");

        /*
         * center
         */
        final ActivityManager centerActivityManager = new ActivityManager(centerActivityMapper,
            eventBus);
        centerActivityManager.setDisplay(centerPanel);

        /*
         * south
         */
        final ActivityManager southActivityManager = new ActivityManager(southActivityMapper,
            eventBus);
        southActivityManager.setDisplay(southPanel);

        /*
         * west
         */
        final ActivityManager westActivityManager = new ActivityManager(westActivityMapper,
            eventBus);
        westActivityManager.setDisplay(westPanel);

        /*
         * east
         */
        final ActivityManager eastActivityManager = new ActivityManager(eastActivityMapper,
            eventBus);
        eastActivityManager.setDisplay(eastPanel);
    }

    HandlerRegistration tabBarSelectionHandler = null;

    @Override
    protected void onLoad() {
        tabBarSelectionHandler = tabBar.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(final SelectionEvent<Integer> event) {
                final Integer selectedItem = event.getSelectedItem();

                final BasePlace basePlace = (BasePlace) placeController.getWhere();
                final Map<String, String> parameters = basePlace.getParameters();
                final String suffix = Logger.format("history=%s",
                    Boolean.valueOf(parameters.get("history")));

                if (selectedItem == 2) {
                    placeController.goTo(new PlaceThree(suffix));

                } else if (selectedItem == 1) {
                    placeController.goTo(new PlaceTwo(suffix));

                } else if (selectedItem == 0) {
                    placeController.goTo(new PlaceOne(suffix));
                }
            }
        });
    }

    @Override
    protected void onUnload() {
        if (tabBarSelectionHandler != null) {
            tabBarSelectionHandler.removeHandler();
        }
    }
}
