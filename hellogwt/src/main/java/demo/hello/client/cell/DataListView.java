package demo.hello.client.cell;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.DatePickerCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;

import demo.hello.client.Logger;
import demo.hello.client.Messages;
import demo.hello.shared.cell.CellDTO;

/**
 * DataViewList
 */
public class DataListView extends ResizeComposite {
    final CellEventHub eventHub;
    final Messages messages;

    final CellTable<CellDTO> cellTable;

    final Column<CellDTO, String> columnA;
    final Column<CellDTO, String> columnB;
    final Column<CellDTO, Boolean> columnC;
    final Column<CellDTO, Date> columnD;

    @Inject
    DataListView(final CellEventHub eventHub, final Messages messages) {
        this.eventHub = eventHub;
        this.messages = messages;

        // cellTable
        cellTable = createTable();

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

        // layoutPanel (simplePager + cellTablePanel)
        final DockLayoutPanel layoutPanel = new DockLayoutPanel(Unit.EM);
        layoutPanel.setSize("100%", "100%");
        layoutPanel.addSouth(simplePager, 2);
        layoutPanel.add(cellTablePanel);

        initWidget(layoutPanel);
    }

    /*
     * candidate for DataListView interface
     */
    public CellTable<CellDTO> cellTable() {
        return cellTable;
    }

    /*
     * candidate for DataListView interface
     */
    public Column<CellDTO, String> columnA() {
        return columnA;
    }

    /*
     * candidate for DataListView interface
     */
    public Column<CellDTO, String> columnB() {
        return columnB;
    }

    /*
     * candidate for DataListView interface
     */
    public Column<CellDTO, Boolean> columnC() {
        return columnC;
    }

    /*
     * candidate for DataListView interface
     */
    public Column<CellDTO, Date> columnD() {
        return columnD;
    }

    /**
     * createTable.
     * 
     * @return a CellTable with: SingleSelectionModel, PageSize=5
     */
    CellTable<CellDTO> createTable() {
        final CellTable<CellDTO> cellTable = new CellTable<CellDTO>();
        cellTable.setWidth("100%");
        cellTable.setHeight("100%");

        cellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.BOUND_TO_SELECTION);
        cellTable.setSelectionModel(new SingleSelectionModel<CellDTO>());
        cellTable.setPageSize(5);
        cellTable.setPageStart(0);
        return cellTable;
    }

    /**
     * addColA
     */
    Column<CellDTO, String> createColumnA() {
        final Column<CellDTO, String> column = new Column<CellDTO, String>(new EditTextCell()) {
            @Override
            public String getValue(final CellDTO object) {
                return object.getA();
            }
        };

        column.setFieldUpdater(new FieldUpdater<CellDTO, String>() {
            public void update(final int index, final CellDTO object, final String value) {
                Logger.logpf(Level.INFO, "column().new FieldUpdater() {...}", "update",
                    "CellDTO[%s].setA(%s)", index, value);

                object.setA(value);
            }
        });

        cellTable.addColumn(column, "Column A");
        return column;
    }

    /**
     * addColB
     */
    Column<CellDTO, String> createColumnB() {
        final Column<CellDTO, String> column = new Column<CellDTO, String>(new EditTextCell()) {
            @Override
            public String getValue(final CellDTO object) {
                return String.valueOf(object.getB());
            }
        };

        column.setFieldUpdater(new FieldUpdater<CellDTO, String>() {
            public void update(final int index, final CellDTO object, final String value) {
                try {
                    final Double valueOf = Double.valueOf(value);
                    Logger.logpf(Level.INFO, "columnB().new FieldUpdater() {...}", "update",
                        "CellDTO[%s].setB(%s)", index, value);

                    object.setB(valueOf);

                } catch (final NumberFormatException e) {
                    Logger.loge(Level.SEVERE, "columnB(...).new FieldUpdater", "update", e);
                    // Logger.logpf(Level.SEVERE, "columnB().new FieldUpdater() {...}", "update",
                    // "CellDTO[%s].setB(%s)", index, value);

                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                        @Override
                        public void execute() {
                            // FIXME: refresh initial value
                            cellTable.setRowData(index, Collections.singletonList(object));
                            cellTable.flush();
                            // RangeChangeEvent.fire(cellTable, new Range(index, 1));
                        }
                    });
                }
            }
        });

        cellTable.addColumn(column, "Column B");
        return column;
    }

    /**
     * addColC
     */
    Column<CellDTO, Boolean> createColumnC() {
        final Column<CellDTO, Boolean> column = new Column<CellDTO, Boolean>(new CheckboxCell()) {
            @Override
            public Boolean getValue(final CellDTO object) {
                return object.getC();
            }
        };

        column.setFieldUpdater(new FieldUpdater<CellDTO, Boolean>() {
            public void update(final int index, final CellDTO object, final Boolean value) {
                Logger.logpf(Level.INFO, "column().new FieldUpdater() {...}", "update",
                    "CellDTO[%s].setC(%s)", index, value);

                object.setC(value);
            }
        });

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
                            CellEventSource.DATA_LIST_VIEW);
                    }
                });
            }
        });

        cellTable.addColumn(column, columnHeader);
        return column;
    }

    /**
     * addColD
     */
    Column<CellDTO, Date> createColumnD() {
        final Column<CellDTO, Date> column = new Column<CellDTO, Date>(new DatePickerCell()) {
            @Override
            public Date getValue(final CellDTO object) {
                return object.getD();
            }
        };

        column.setFieldUpdater(new FieldUpdater<CellDTO, Date>() {
            public void update(final int index, final CellDTO object, final Date value) {
                Logger.logpf(Level.INFO, "column().new FieldUpdater() {...}", "update",
                    "CellDTO[%s].setD(%tT)", index, value);

                object.setD(value);
            }
        });

        cellTable.addColumn(column, "Column D");
        return column;
    }

    Column<CellDTO, CellDTO> createColumnEdit() {

        final Column<CellDTO, CellDTO> column = new Column<CellDTO, CellDTO>(
            new ActionCell<CellDTO>(messages.edit(), new Delegate<CellDTO>() {
                public void execute(final CellDTO row) {
                    Logger.logp(Level.INFO, "DataListView", "Edit", row.getSummary());
                    eventHub.fireFromSource(new CellEditEvent(row), CellEventSource.DATA_LIST_VIEW);
                }

            })) {
            @Override
            public CellDTO getValue(final CellDTO row) {
                return row;
            }
        };

        cellTable.addColumn(column);
        return column;
    }

}
