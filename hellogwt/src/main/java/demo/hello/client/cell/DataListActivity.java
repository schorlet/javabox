package demo.hello.client.cell;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;

import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.inject.Inject;

import demo.hello.client.Logger;
import demo.hello.client.activity.BaseActivity;
import demo.hello.client.cell.CellEditEvent.CellEditHandler;
import demo.hello.client.cell.CellFilterEvent.CellFilterHandler;
import demo.hello.client.place.BasePlace;
import demo.hello.shared.cell.CellDTO;

/**
 * DataAsyncActivity
 */
public class DataListActivity extends BaseActivity {
    final CellEventHub eventHub;
    final DataListView dataListView;

    @Inject
    DataListActivity(final CellEventHub eventHub, final DataListView dataListView) {
        this.eventHub = eventHub;
        this.dataListView = dataListView;
    }

    @Override
    public void start(final AcceptsOneWidget containerWidget,
        final com.google.gwt.event.shared.EventBus eventBus) {

        Logger.logp(Level.INFO, this.toString(), "start");

        if (dataListView == null) {
            containerWidget.setWidget(null);

        } else {
            containerWidget.setWidget(dataListView.asWidget());
            startActivity();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        listDataProvider.removeDataDisplay(dataListView.cellTable());
        listDataProvider = null;

        columnASelection = null;
        columnCSelection = null;
    }

    /*
     * activity state: 
     */

    ListDataProvider<CellDTO> listDataProvider;

    String columnCSelection;
    String columnASelection;
    boolean columnARegex = false;

    @Override
    public void setPlace(final BasePlace place) {
        final Map<String, String> parameters = place.getParameters();

        if (parameters.containsKey("a")) {
            columnASelection = parameters.get("a");
        }
        if (parameters.containsKey("c")) {
            columnCSelection = parameters.get("c");
        }
        if (parameters.containsKey("re")) {
            columnARegex = Boolean.valueOf(parameters.get("re"));
        }

        // ListDataProvider
        listDataProvider = new ListDataProvider<CellDTO>();
    }

    /**
     * start activity
     */
    void startActivity() {
        initCellTable();
        registerEventsHandlers();
        refreshCellTable();
    }

    /**
     * initialize cellTable
     */
    void initCellTable() {
        final CellTable<CellDTO> cellTable = dataListView.cellTable();

        // clear column sorting
        cellTable.getColumnSortList().clear();

        final Comparator<CellDTO> comparatorA = initColumnA(dataListView.columnA());
        final Comparator<CellDTO> comparatorB = initColumnB(dataListView.columnB());
        // final Comparator<CellDTO> comparatorC = initColumnC(dataListView.columnC());
        final Comparator<CellDTO> comparatorD = initColumnD(dataListView.columnD());

        // columnSortHandler
        final ColumnSortEvent.ListHandler<CellDTO> columnSortHandler = new ColumnSortEvent.ListHandler<CellDTO>(
            listDataProvider.getList());

        columnSortHandler.setComparator(dataListView.columnA(), comparatorA);
        columnSortHandler.setComparator(dataListView.columnB(), comparatorB);
        // columnSortHandler.setComparator(dataListView.columnC(), comparatorC);
        columnSortHandler.setComparator(dataListView.columnD(), comparatorD);

        // register column sort handler
        registerHandler(cellTable.addColumnSortHandler(columnSortHandler));
    }

    /**
     * register events handlers
     */
    void registerEventsHandlers() {
        // columnC FilterHandler
        registerHandler(eventHub.addHandlerToSource(CellFilterEvent.TYPE,
            CellEventSource.DATA_LIST_VIEW, new CellFilterHandler() {
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
                    columnARegex = event.isRegex();
                    refreshCellTable();
                }
            }));

        // cellEditHandler
        registerHandler(eventHub.addHandlerToSource(CellEditEvent.TYPE,
            CellEventSource.CELLDTO_EDITOR_VIEW, new CellEditHandler() {
                @Override
                public void onEditCell(final CellEditEvent event) {
                    refreshCellTable();
                }
            }));
    }

    /**
     * refresh cellTable
     */
    void refreshCellTable() {
        Logger.logpf(Level.INFO, "DataListActivity", "refreshCellTable", "a=%s, c=%s",
            columnASelection, columnCSelection);

        final List<CellDTO> listData = listDataProvider.getList();
        listData.clear();
        listData.addAll(CellDtoProvider.getCelldtoList());

        if (columnCSelection != null && !columnCSelection.equals("all")) {
            final boolean selection = Boolean.valueOf(columnCSelection);

            final ListIterator<CellDTO> listIterator = listData.listIterator();
            while (listIterator.hasNext()) {
                final Boolean c = listIterator.next().getC();
                if (c ^ selection) { // xor
                    listIterator.remove();
                }
            }
        }

        if (columnASelection != null && !columnASelection.isEmpty()) {
            final String selection = columnARegex ? columnASelection : columnASelection
                .toLowerCase();

            final ListIterator<CellDTO> listIterator = listData.listIterator();
            while (listIterator.hasNext()) {
                final String a = listIterator.next().getA();
                if (a == null || a.isEmpty()) {
                    listIterator.remove();

                } else if (columnARegex) {
                    if (!a.matches(selection)) {
                        listIterator.remove();
                    }
                } else if (!a.toLowerCase().startsWith(selection)) {
                    listIterator.remove();
                }
            }
        }

        final CellTable<CellDTO> cellTable = dataListView.cellTable();

        if (listDataProvider.getDataDisplays().contains(cellTable)) {
            listDataProvider.refresh();
        } else {
            // bind cellTable to listDataProvider
            listDataProvider.addDataDisplay(cellTable);
        }
    }

    Comparator<CellDTO> initColumnA(final Column<CellDTO, String> columnA) {
        columnA.setSortable(true);

        return new Comparator<CellDTO>() {
            @Override
            public int compare(final CellDTO o1, final CellDTO o2) {
                return o1.getA().compareTo(o2.getA());
            }
        };
    }

    Comparator<CellDTO> initColumnB(final Column<CellDTO, String> columB) {
        columB.setSortable(true);

        return new Comparator<CellDTO>() {
            @Override
            public int compare(final CellDTO o1, final CellDTO o2) {
                return o1.getB().compareTo(o2.getB());
            }
        };
    }

    Comparator<CellDTO> initColC(final Column<CellDTO, Boolean> columnC) {
        columnC.setSortable(true);

        return new Comparator<CellDTO>() {
            @Override
            public int compare(final CellDTO o1, final CellDTO o2) {
                return o1.getC().compareTo(o2.getC());
            }
        };
    }

    Comparator<CellDTO> initColumnD(final Column<CellDTO, Date> columnD) {
        columnD.setSortable(true);

        return new Comparator<CellDTO>() {
            @Override
            public int compare(final CellDTO o1, final CellDTO o2) {
                return o1.getD().compareTo(o2.getD());
            }
        };
    }
}
