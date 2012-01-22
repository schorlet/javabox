package demo.hello.client.cell;

import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.DatePickerCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;

import demo.hello.client.Logger;
import demo.hello.client.Messages;
import demo.hello.shared.cell.CellProxy;

/**
 * DataViewAsync
 */
public class DataAsyncView extends ResizeComposite {
    final CellEventHub eventHub;
    final Messages messages;

    final CellTable<CellProxy> cellTable;

    final CellColumn<CellProxy, String> columnA;
    final CellColumn<CellProxy, String> columnB;
    final CellColumn<CellProxy, Boolean> columnC;
    final CellColumn<CellProxy, Date> columnD;

    final Button buttonSave;

    @Inject
    DataAsyncView(final CellEventHub eventHub, final Messages messages) {
        this.eventHub = eventHub;
        this.messages = messages;

        // cellTable
        cellTable = createCellTable();

        // cellTable columns
        columnA = createColumnA();
        columnB = createColumnB();
        columnC = createColumnC();
        columnD = createColumnD();
        createColumnEdit();

        // cellTablePanel
        final ScrollPanel cellTablePanel = new ScrollPanel(cellTable);
        cellTablePanel.setSize("100%", "100%");

        // simplePager
        final SimplePager simplePager = new SimplePager(SimplePager.TextLocation.CENTER);
        simplePager.setSize("100%", "100%");
        simplePager.setDisplay(cellTable);

        // buttonSave
        buttonSave = new Button(messages.save());
        buttonSave.setWidth("6em");

        // southPanel (simplePager + buttonSave)
        final DockLayoutPanel southPanel = new DockLayoutPanel(Unit.PCT);
        southPanel.setSize("100%", "100%");
        southPanel.addEast(buttonSave, 16);
        southPanel.add(simplePager);

        // layoutPanel (southPanel + cellTablePanel)
        final DockLayoutPanel layoutPanel = new DockLayoutPanel(Unit.EM);
        layoutPanel.setSize("100%", "100%");
        layoutPanel.addSouth(southPanel, 2);
        layoutPanel.add(cellTablePanel);

        initWidget(layoutPanel);
    }

    /*
     * candidate for DataAsyncView interface
     */
    public CellTable<CellProxy> cellTable() {
        return cellTable;
    }

    /*
     * candidate for DataAsyncView interface
     */
    public CellColumn<CellProxy, String> columnA() {
        return columnA;
    }

    /*
     * candidate for DataAsyncView interface
     */
    public CellColumn<CellProxy, String> columnB() {
        return columnB;
    }

    /*
     * candidate for DataAsyncView interface
     */
    public CellColumn<CellProxy, Boolean> columnC() {
        return columnC;
    }

    /*
     * candidate for DataAsyncView interface
     */
    public CellColumn<CellProxy, Date> columnD() {
        return columnD;
    }

    /*
     * candidate for DataAsyncView interface
     */
    public FocusWidget buttonSave() {
        return buttonSave;
    }

    /**
     * createTable.
     * 
     * @return a CellTable with: SingleSelectionModel, PageSize=5
     */
    CellTable<CellProxy> createCellTable() {
        final CellTable<CellProxy> cellTable = new CellTable<CellProxy>(
            CellClient.CELL_PROXY_KEY_PROVIDER);
        cellTable.setWidth("100%");
        cellTable.setHeight("100%");

        cellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);
        cellTable.setSelectionModel(new SingleSelectionModel<CellProxy>());
        cellTable.setPageSize(5);
        cellTable.setPageStart(0);
        return cellTable;
    }

    CellColumn<CellProxy, String> createColumnA() {
        final CellColumn<CellProxy, String> column = new CellColumn<CellProxy, String>("a",
            new EditTextCell()) {
            @Override
            public String getValue(final CellProxy object) {
                return object.getA();
            }
        };

        cellTable.addColumn(column, "Column A");
        return column;
    }

    CellColumn<CellProxy, String> createColumnB() {
        final CellColumn<CellProxy, String> column = new CellColumn<CellProxy, String>("b",
            new EditTextCell()) {
            @Override
            public String getValue(final CellProxy object) {
                return String.valueOf(object.getB());
            }
        };

        cellTable.addColumn(column, "Column B");
        return column;
    }

    CellColumn<CellProxy, Boolean> createColumnC() {
        final CellColumn<CellProxy, Boolean> column = new CellColumn<CellProxy, Boolean>("c",
            new CheckboxCell()) {
            @Override
            public Boolean getValue(final CellProxy object) {
                return object.getC();
            }
        };

        final SelectionCell selectionCell = new SelectionCell(Arrays.asList("all", "true", "false"));

        final Header<String> columnHeader = new Header<String>(selectionCell) {
            @Override
            public String getValue() {
                return "fucking shit";
            }
        };

        columnHeader.setUpdater(new ValueUpdater<String>() {
            @Override
            public void update(final String value) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        eventHub.fireFromSource(new CellFilterEvent(value),
                            CellEventSource.DATA_ASYNC_VIEW);
                    }
                });
            }
        });

        cellTable.addColumn(column, columnHeader);
        return column;
    }

    CellColumn<CellProxy, Date> createColumnD() {
        final CellColumn<CellProxy, Date> column = new CellColumn<CellProxy, Date>("d",
            new DatePickerCell()) {
            @Override
            public Date getValue(final CellProxy object) {
                return object.getD();
            }
        };

        cellTable.addColumn(column, "Column D");
        return column;
    }

    CellColumn<CellProxy, CellProxy> createColumnEdit() {
        final CellColumn<CellProxy, CellProxy> column = new CellColumn<CellProxy, CellProxy>(
            "Summary", new ActionCell<CellProxy>(messages.edit(), new Delegate<CellProxy>() {
                public void execute(final CellProxy row) {
                    Logger.logpf(Level.INFO, "DataAsyncView", "Edit",
                        "a: %s, b: %s, c: %s, d: %tT, id: %s", row.getA(), row.getB(), row.getC(),
                        row.getD(), row.getId());

                    eventHub
                        .fireFromSource(new CellEditEvent(row), CellEventSource.DATA_ASYNC_VIEW);
                }
            })) {
            @Override
            public CellProxy getValue(final CellProxy row) {
                return row;
            }
        };

        cellTable.addColumn(column);
        return column;
    }

}
