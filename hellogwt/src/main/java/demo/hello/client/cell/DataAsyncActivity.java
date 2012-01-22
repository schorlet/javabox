package demo.hello.client.cell;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import demo.hello.client.Logger;
import demo.hello.client.activity.BaseActivity;
import demo.hello.client.cell.CellEditEvent.CellEditHandler;
import demo.hello.client.cell.CellFilterEvent.CellFilterHandler;
import demo.hello.client.place.BasePlace;
import demo.hello.shared.ProjectRequestFactory.CellRequest;
import demo.hello.shared.cell.CellProxy;

/**
 * DataAsyncActivity
 */
public class DataAsyncActivity extends BaseActivity {
    final CellEventHub eventHub;
    final DataAsyncView dataAsyncView;
    final CellClient cellClient;

    @Inject
    DataAsyncActivity(final CellEventHub eventHub, final DataAsyncView dataAsyncView,
        final CellClient cellClient) {

        this.eventHub = eventHub;
        this.dataAsyncView = dataAsyncView;
        this.cellClient = cellClient;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget,
        final com.google.gwt.event.shared.EventBus eventBus) {
        Logger.logp(Level.INFO, this.toString(), "start");

        if (dataAsyncView == null) {
            containerWidget.setWidget(null);

        } else {
            containerWidget.setWidget(dataAsyncView.asWidget());
            startActivity();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        asyncCellProvider.removeDataDisplay(dataAsyncView.cellTable());
        asyncCellProvider = null;

        newCellRequest = null;
        columnASelection = null;
        columnCSelection = null;
    }

    /*
     * activity state:
     */

    // cellTable dataProvider
    AsyncCellProvider asyncCellProvider;

    // cell request (renewed after each fire() invocation)
    CellRequest newCellRequest;

    String columnASelection;
    String columnCSelection;

    @Override
    public void setPlace(final BasePlace place) {
        final Map<String, String> parameters = place.getParameters();

        if (parameters.containsKey("a")) {
            columnASelection = parameters.get("a");
        }
        if (parameters.containsKey("c")) {
            columnCSelection = parameters.get("c");
        }

        // newCellRequest
        newCellRequest = cellClient.newCellRequest();
        dataAsyncView.buttonSave().setEnabled(false);

        // asyncDataProvider
        asyncCellProvider = new AsyncCellProvider(cellClient);
    }

    void startActivity() {
        initCellTable();
        registerEventsHandlers();
        refreshCellTable();
    }

    void initCellTable() {
        final CellTable<CellProxy> cellTable = dataAsyncView.cellTable();

        // clear column sorting
        cellTable.getColumnSortList().clear();

        // create FieldUpdater on columns
        initColumnA(dataAsyncView.columnA());
        initColumnB(dataAsyncView.columnB());
        initColumnC(dataAsyncView.columnC());
        initColumnD(dataAsyncView.columnD());

        // register column sort handler
        registerHandler(cellTable.addColumnSortHandler(new ColumnSortEvent.Handler() {
            @Override
            public void onColumnSort(final ColumnSortEvent event) {
                refreshCellTable();
            }
        }));
    }

    void registerEventsHandlers() {
        // columnC FilterHandler
        registerHandler(eventHub.addHandlerToSource(CellFilterEvent.TYPE,
            CellEventSource.DATA_ASYNC_VIEW, new CellFilterHandler() {
                @Override
                public void onFilterCell(final CellFilterEvent event) {
                    columnCSelection = event.getC();
                    refreshCellTable();
                }
            }));

        // columnA FilterHandler
        registerHandler(eventHub.addHandlerToSource(CellFilterEvent.TYPE,
            CellEventSource.SELECTION_ACTIVITY, new CellFilterHandler() {
                @Override
                public void onFilterCell(final CellFilterEvent event) {
                    columnASelection = event.getA();
                    refreshCellTable();
                }
            }));

        // cellEditHandler
        registerHandler(eventHub.addHandlerToSource(CellEditEvent.TYPE,
            CellEventSource.CELLPROXY_EDITOR_VIEW, new CellEditHandler() {
                @Override
                public void onEditCell(final CellEditEvent event) {
                    refreshCellTable();
                }
            }));

        // buttonSaveHandler
        registerHandler(dataAsyncView.buttonSave().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                onButtonSaveClick();
            }
        }));
    }

    void refreshCellTable() {
        Logger.logpf(Level.INFO, "DataAsyncActivity", "refreshCellTable", "a=%s, c=%s",
            columnASelection, columnCSelection);

        final CellTable<CellProxy> cellTable = dataAsyncView.cellTable();

        // newCellRequest
        newCellRequest = cellClient.newCellRequest();
        dataAsyncView.buttonSave().setEnabled(false);

        // asyncDataProvider
        asyncCellProvider.filterOn(columnASelection, columnCSelection);
        asyncCellProvider.sortOn(cellTable.getColumnSortList());

        if (asyncCellProvider.getDataDisplays().contains(cellTable)) {
            asyncCellProvider.onRangeChanged(cellTable);
        } else {
            // bind cellTable to asyncCellProvider
            asyncCellProvider.addDataDisplay(cellTable);
        }
    }

    void onButtonSaveClick() {
        dataAsyncView.buttonSave().setEnabled(false);

        if (newCellRequest.isChanged()) {
            Logger.logp(Level.INFO, "DataAsyncActivity", "onButtonSaveClick");

            newCellRequest.fire(new Receiver<Void>() {
                @Override
                public void onSuccess(final Void response) {
                    refreshCellTable();
                }

                @Override
                public void onFailure(final ServerFailure error) {
                    Logger.logp(Level.SEVERE, "DataAsyncActivity.onButtonSaveClick", "onFailure",
                        error.getMessage(), error.getStackTraceString());
                    Window.alert(error.getMessage());
                };
            });
        }
    }

    /**
     * initColA
     * @param columnA
     */
    private void initColumnA(final Column<CellProxy, String> columnA) {
        columnA.setSortable(true);
        columnA.setFieldUpdater(new FieldUpdater<CellProxy, String>() {
            public void update(final int index, final CellProxy object, final String value) {
                Logger.logpf(Level.INFO, "columnA(...).new FieldUpdater", "update",
                    "CellProxy[%s].setA(%s)", index, value);

                newCellRequest.updateCell(object);
                final CellProxy edit = newCellRequest.edit(object);
                edit.setA(value);
                dataAsyncView.buttonSave().setEnabled(true);
            }
        });
    }

    /**
     * initColB
     * @param columnB
     */
    private void initColumnB(final Column<CellProxy, String> columnB) {
        columnB.setSortable(true);

        columnB.setFieldUpdater(new FieldUpdater<CellProxy, String>() {
            public void update(final int index, final CellProxy object, final String value) {
                try {
                    final Double valueOf = Double.valueOf(value);

                    Logger.logpf(Level.INFO, "columnB(...).new FieldUpdater", "update",
                        "CellProxy[%s].setB(%s)", index, value);

                    newCellRequest.updateCell(object);
                    final CellProxy edit = newCellRequest.edit(object);
                    edit.setB(valueOf);
                    dataAsyncView.buttonSave().setEnabled(true);

                } catch (final NumberFormatException e) {
                    Logger.loge(Level.SEVERE, "columnB(...).new FieldUpdater", "update", e);
                    // Logger.logpf(Level.SEVERE, "columnB(...).new FieldUpdater", "update",
                    // "CellProxy[%s].setB(%s)", index, value);

                    // TODO: refresh actual values
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                        @Override
                        public void execute() {
                            dataAsyncView.cellTable().setRowData(index,
                                Collections.singletonList(object));
                            dataAsyncView.cellTable().flush();
                            dataAsyncView.cellTable().flush();
                        }
                    });
                }
            }
        });
    }

    /**
     * initColC
     * @param columnC
     */
    private void initColumnC(final Column<CellProxy, Boolean> columnC) {
        columnC.setSortable(false);

        columnC.setFieldUpdater(new FieldUpdater<CellProxy, Boolean>() {
            public void update(final int index, final CellProxy object, final Boolean value) {
                Logger.logpf(Level.INFO, "columnC(...).new FieldUpdater", "update",
                    "CellProxy[%s].setC(%s)", index, value);

                newCellRequest.updateCell(object);
                final CellProxy edit = newCellRequest.edit(object);
                edit.setC(value);
                dataAsyncView.buttonSave().setEnabled(true);
            }
        });
    }

    /**
     * initColD
     * @param columnD
     */
    private void initColumnD(final Column<CellProxy, Date> columnD) {
        columnD.setSortable(true);

        columnD.setFieldUpdater(new FieldUpdater<CellProxy, Date>() {
            public void update(final int index, final CellProxy object, final Date value) {
                Logger.logpf(Level.INFO, "columnD(...).new FieldUpdater", "update",
                    "CellProxy[%s].setD(%s)", index, value);

                newCellRequest.updateCell(object);
                final CellProxy edit = newCellRequest.edit(object);
                edit.setD(value);
                dataAsyncView.buttonSave().setEnabled(true);
            }
        });
    }
}
