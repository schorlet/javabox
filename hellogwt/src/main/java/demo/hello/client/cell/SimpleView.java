package demo.hello.client.cell;

import java.util.ArrayList;
import java.util.logging.Level;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.IconCellDecorator;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextButtonCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.CellWidget;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.PageSizePager;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.inject.Inject;

import demo.hello.client.Logger;
import demo.hello.client.Resources;

/**
 * SimpleView
 */
public class SimpleView extends ResizeComposite implements ClickHandler {
    final Resources resources;

    final VerticalPanel leftPanel = new VerticalPanel();
    final VerticalPanel rightPanel = new VerticalPanel();

    @Inject
    SimpleView(final Resources resources) {
        this.resources = resources;

        createLeftPanel();
        createRightPanel();

        leftPanel.setSize("100%", "100%");
        leftPanel.setSpacing(2);
        rightPanel.setSize("100%", "100%");
        rightPanel.setSpacing(2);

        final ScrollPanel leftScrollPanel = new ScrollPanel(leftPanel);
        final ScrollPanel rightScrollPanel = new ScrollPanel(rightPanel);

        final DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.PCT);
        dockLayoutPanel.setSize("100%", "100%");
        dockLayoutPanel.addWest(leftScrollPanel, 75);
        dockLayoutPanel.add(rightScrollPanel);

        initWidget(dockLayoutPanel);
    }

    /**
     * createRightPanel
     */
    void createRightPanel() {
        createtextButton();
        createbutton();
        createlink();
        createnumber();
        createlog();
        createclear();
    }

    public void createLeftPanel() {
        // create some data
        final ArrayList<String> values = new ArrayList<String>();
        values.add("one");
        values.add("two");
        values.add("three");
        values.add("four");
        values.add("five");
        values.add("six");

        // create a ListDataProvider
        final ListDataProvider<String> ldp = new ListDataProvider<String>();
        // give the ListDataProvider our data
        ldp.setList(values);

        demo1(ldp);
        demo2(ldp);
        demo3(ldp);
        demo4(ldp);
    }

    void createtextButton() {
        final TextButtonCell textButtonCell = new TextButtonCell();
        textButtonCell.setIcon(resources.cross());
        final CellWidget<String> textButtonWidget = new CellWidget<String>(textButtonCell, "");

        textButtonWidget.addHandler(this, ClickEvent.getType());
        rightPanel.add(textButtonWidget);
    }

    void createbutton() {
        final ButtonCell buttonCell = new ButtonCell();
        final CellWidget<String> buttonWidget = new CellWidget<String>(buttonCell, "buttonCell");

        buttonWidget.addHandler(this, ClickEvent.getType());
        rightPanel.add(buttonWidget);
    }

    void createlink() {
        final Image image = new Image(resources.cross());
        DOM.setStyleAttribute(image.getElement(), "backgroundColor", "lightgray");

        final Anchor anchor = new Anchor();
        anchor.sinkEvents(Event.ONCLICK);
        anchor.addHandler(this, ClickEvent.getType());

        DOM.insertBefore(anchor.getElement(), image.getElement(),
            DOM.getFirstChild(anchor.getElement()));

        final SimplePanel panel = new SimplePanel(anchor);
        panel.setStylePrimaryName("imgButton2");

        rightPanel.add(panel);
    }

    CellWidget<Number> numberWidget;

    void createnumber() {
        final NumberCell numberCell = new NumberCell(NumberFormat.getDecimalFormat());
        numberWidget = new CellWidget<Number>(numberCell, Random.nextDouble());

        numberWidget.addValueChangeHandler(new ValueChangeHandler<Number>() {
            @Override
            public void onValueChange(final ValueChangeEvent<Number> event) {
                Logger.logp(Level.INFO, "NumberCell", "onValueChange", event);
            }
        });
        numberWidget.addHandler(this, ClickEvent.getType());
        rightPanel.add(numberWidget);
    }

    void createlog() {
        final ActionCell<String> logCell = new ActionCell<String>("log", new Delegate<String>() {
            @Override
            public void execute(final String object) {
                Logger.logp(Level.INFO, "logCell", "execute", object);
                numberWidget.setValue(Random.nextDouble());
            }
        });
        final CellWidget<String> logWidget = new CellWidget<String>(logCell, "logCell");
        rightPanel.add(logWidget);
    }

    void createclear() {
        final ActionCell<String> clearCell = new ActionCell<String>("clear",
            new Delegate<String>() {
                @Override
                public void execute(final String object) {
                    Logger.logp(Level.INFO, "clearCell", "execute", object);
                }
            });

        final IconCellDecorator<String> clearDecorator = new IconCellDecorator<String>(
            resources.cross(), clearCell, HasVerticalAlignment.ALIGN_TOP, 0);
        final CellWidget<String> clearWidget = new CellWidget<String>(clearDecorator, "clearCell");
        rightPanel.add(clearWidget);
    }

    @Override
    public void onClick(final ClickEvent event) {
        numberWidget.setValue(Random.nextDouble());
        Logger
            .logp(Level.INFO, "SimpleView", "onClick", numberWidget.getValue(), event.getSource());
    }

    void demo1(final ListDataProvider<String> ldp) {
        // CellList of TextCells with PageSizePager
        final CellList<String> cl = new CellList<String>(new TextCell());
        // set the initial pagesize to 2
        cl.setPageSize(2);

        // add the CellLists to the adaptor
        ldp.addDataDisplay(cl);

        // create a PageSizePager, giving it a handle to the CellList
        final PageSizePager psp = new PageSizePager(2);
        psp.setDisplay(cl);

        // add the CellList to the page
        leftPanel.add(cl);

        // add the PageSizePager to the page
        leftPanel.add(psp);
    }

    void demo2(final ListDataProvider<String> ldp) {
        // CellList of TextCells with a SimplePager
        final CellList<String> cl = new CellList<String>(new TextCell());
        // set the initial pageSize to 2
        cl.setPageSize(2);

        // add the CellLists to the adaptor
        ldp.addDataDisplay(cl);

        // create a pager, giving it a handle to the CellList
        final SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER);
        pager.setDisplay(cl);

        // add the CellList to the page
        leftPanel.add(cl);

        // add the Pager to the page
        leftPanel.add(pager);
    }

    void demo3(final ListDataProvider<String> ldp) {
        // CellList of TextCells with a SimplePager and PageSizePager
        final CellList<String> cl = new CellList<String>(new TextCell());
        // set the initial pageSize to 2
        cl.setPageSize(2);

        // add the CellLists to the adaptor
        ldp.addDataDisplay(cl);

        // create a PageSizePager, giving it a handle to the CellList
        final PageSizePager psp = new PageSizePager(1);
        psp.setDisplay(cl);

        // create a pager, giving it a handle to the CellList
        final SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER);
        pager.setDisplay(cl);

        // add the CellList to the page
        leftPanel.add(cl);

        // add the Pager to the page
        leftPanel.add(pager);

        // add the PageSizePager to the page
        leftPanel.add(psp);
    }

    void demo4(final ListDataProvider<String> ldp) {
        // CellTable
        final CellTable<String> ct = new CellTable<String>();
        ct.setPageSize(2);
        ldp.addDataDisplay(ct);

        // add a column with a simple string header
        ct.addColumn(new TextColumn<String>() {
            @Override
            public String getValue(final String object) {
                return object;
            }
        }, "String Header");

        // add a column with a TextCell header
        ct.addColumn(new TextColumn<String>() {
            @Override
            public String getValue(final String object) {
                return "%" + object + "%";
            }
        }, new Header<String>(new TextCell()) {
            @Override
            public String getValue() {
                return "TextCell Header";
            }
        });

        // create a pager, giving it a handle to the CellTable
        final SimplePager pager = new SimplePager(SimplePager.TextLocation.CENTER);
        pager.setDisplay(ct);

        // add the CellList to the page
        leftPanel.add(ct);

        // add the Pager to the page
        leftPanel.add(pager);
    }
}
