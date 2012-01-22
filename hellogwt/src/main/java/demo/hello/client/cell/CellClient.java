package demo.hello.client.cell;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import com.google.gwt.view.client.ProvidesKey;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.requestfactory.shared.Receiver;

import demo.hello.client.Logger;
import demo.hello.shared.FilterConstraintOpEnum;
import demo.hello.shared.FilterConstraintProxy;
import demo.hello.shared.FilterConstraintTypeEnum;
import demo.hello.shared.ProjectRequestFactory;
import demo.hello.shared.ProjectRequestFactory.CellRequest;
import demo.hello.shared.SortConstraintProxy;
import demo.hello.shared.cell.CellProxy;

/**
 * CellClient
 */
public class CellClient {
    protected final ProjectRequestFactory requestFactory;

    @Inject
    protected CellClient(final Provider<ProjectRequestFactory> projectRequestFactory) {
        requestFactory = projectRequestFactory.get();
    }

    /**
     * newCellRequest
     */
    public CellRequest newCellRequest() {
        return requestFactory.cellRequest();
    }

    /**
     * newSortConstraintProxy
     */
    public SortConstraintProxy newSortConstraintProxy(final String column, final boolean ascending,
        final CellRequest cellRequest) {

        final SortConstraintProxy sortConstraintProxy = cellRequest
            .create(SortConstraintProxy.class);

        sortConstraintProxy.setColumn(column);
        sortConstraintProxy.setAscending(ascending);

        return sortConstraintProxy;
    }

    public FilterConstraintProxy newFilterConstraintProxy(final String column, final String value,
        final FilterConstraintTypeEnum type, final CellRequest cellRequest) {
        return newFilterConstraintProxy(column, value, type, FilterConstraintOpEnum.EQ, cellRequest);
    }

    public FilterConstraintProxy newFilterConstraintProxy(final String column, final String value,
        final FilterConstraintTypeEnum type, final FilterConstraintOpEnum op,
        final CellRequest cellRequest) {

        final FilterConstraintProxy filterConstraintProxy = cellRequest
            .create(FilterConstraintProxy.class);

        filterConstraintProxy.setColumn(column);
        filterConstraintProxy.setValue(value);
        filterConstraintProxy.setType(type);
        filterConstraintProxy.setOp(op);

        return filterConstraintProxy;
    }

    /**
     * listCell. fire the cellRequest. countReceiver is optional
     */
    public void listCellAndCount(final int start, final int end,
        final Set<FilterConstraintProxy> filterConstraints,
        final Set<SortConstraintProxy> sortConstraints, final CellRequest cellRequest,
        final Receiver<List<CellProxy>> listReceiver, final Receiver<Long> countReceiver) {

        Logger.logp(Level.INFO, "CellClient", "listCellAndCount", "start: " + start, "end: " + end,
            "filterConstraints: " + filterToString(filterConstraints), "sortConstraints: "
                + sortToString(sortConstraints));

        if (countReceiver != null) {
            cellRequest.countCell(filterConstraints).to(countReceiver);
        }

        cellRequest.listCell(start, end, filterConstraints, sortConstraints).to(listReceiver);
        cellRequest.fire();
    }

    /**
     * findCell
     */
    public void findCell(final Integer id, final Receiver<CellProxy> receiver) {
        Logger.logp(Level.INFO, "CellClient", "findCell", "id: " + id);
        newCellRequest().findCell(id).fire(receiver);
    }

    protected String filterToString(final Set<FilterConstraintProxy> filterConstraints) {
        final StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (final FilterConstraintProxy filterConstraint : filterConstraints) {
            builder.append(" [column=").append(filterConstraint.getColumn()).append(", op=")
                .append(filterConstraint.getOp()).append(", value=")
                .append(filterConstraint.getValue()).append(", type=")
                .append(filterConstraint.getType()).append("], ");
        }
        builder.append("}");
        return builder.toString();
    }

    protected String sortToString(final Set<SortConstraintProxy> sortConstraints) {
        final StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (final SortConstraintProxy sortConstraint : sortConstraints) {
            builder.append(" [column=").append(sortConstraint.getColumn()).append(", ascending=")
                .append(sortConstraint.isAscending()).append("], ");
        }
        builder.append("}");
        return builder.toString();
    }

    public static ProvidesKey<CellProxy> CELL_PROXY_KEY_PROVIDER = new ProvidesKey<CellProxy>() {
        @Override
        public Object getKey(final CellProxy cellProxy) {
            return cellProxy.getId();
        }
    };
}
