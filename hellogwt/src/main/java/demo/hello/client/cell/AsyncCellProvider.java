package demo.hello.client.cell;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import demo.hello.client.Logger;
import demo.hello.shared.FilterConstraintOpEnum;
import demo.hello.shared.FilterConstraintProxy;
import demo.hello.shared.FilterConstraintTypeEnum;
import demo.hello.shared.ProjectRequestFactory.CellRequest;
import demo.hello.shared.SortConstraintProxy;
import demo.hello.shared.cell.CellProxy;

/**
 * AsyncCellProvider
 */
public class AsyncCellProvider extends AsyncDataProvider<CellProxy> {
    final CellClient cellClient;

    AsyncCellProvider(final CellClient cellClient) {
        super(CellClient.CELL_PROXY_KEY_PROVIDER);
        this.cellClient = cellClient;
    }

    @Override
    public void onRangeChanged(final HasData<CellProxy> display) {
        final CellRequest newCellRequest = cellClient.newCellRequest();

        // range constraints
        final Range range = display.getVisibleRange();
        final int start = range.getStart();
        final int end = start + range.getLength();

        Logger.logp(Level.FINE, "AsyncCellProvider", "onRangeChanged", "(start:" + start, "end:"
            + end, "sort:" + columnSortList, "filters: {c=" + filterC + "})");

        // sort constraints
        final Set<SortConstraintProxy> sortConstraints = new LinkedHashSet<SortConstraintProxy>();

        if (columnSortList != null) {
            final int size = columnSortList.size();
            for (int i = 0; i < size; i++) {
                final ColumnSortInfo columnSortInfo = columnSortList.get(i);
                final String columnName = ((CellColumn<?, ?>) columnSortInfo.getColumn())
                    .getColumnName();

                sortConstraints.add(cellClient.newSortConstraintProxy(columnName,
                    columnSortInfo.isAscending(), newCellRequest));
            }
        }

        // filter constraints
        final Set<FilterConstraintProxy> filterConstraints = new LinkedHashSet<FilterConstraintProxy>();
        if (filterC != null && !filterC.equals("all")) {
            filterConstraints.add(cellClient.newFilterConstraintProxy("c", filterC,
                FilterConstraintTypeEnum.BOOLEAN, newCellRequest));
        }
        if (filterA != null && !filterA.isEmpty()) {
            filterConstraints.add(cellClient.newFilterConstraintProxy("a", filterA,
                FilterConstraintTypeEnum.STRING, FilterConstraintOpEnum.SW, newCellRequest));
        }

        cellClient.listCellAndCount(start, end, filterConstraints, sortConstraints, newCellRequest,
        // updateRowData
            new Receiver<List<CellProxy>>() {
                @Override
                public void onSuccess(final List<CellProxy> response) {
                    Logger.logp(Level.FINE, "CellClient", "listCell", response.size());
                    updateRowData(start, response);
                }

                @Override
                public void onFailure(final ServerFailure error) {}
            },
            // updateRowCount
            new Receiver<Long>() {
                @Override
                public void onSuccess(final Long response) {
                    Logger.logp(Level.INFO, "CellClient", "countCell", response);
                    updateRowCount(response.intValue(), true);
                }

                @Override
                public void onFailure(final ServerFailure error) {
                    Logger.logp(Level.SEVERE, "CellClient", "onFailure", error.getMessage(),
                        error.getStackTraceString());
                    Window.alert(error.getMessage());
                }
            });
    }

    ColumnSortList columnSortList = null;

    public void sortOn(final ColumnSortList columnSortList) {
        this.columnSortList = columnSortList;
    }

    String filterA = null;
    String filterC = null;

    public void filterOn(final String filterA, final String filterC) {
        this.filterA = filterA;
        this.filterC = filterC;
    }
}
