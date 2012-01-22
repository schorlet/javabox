package demo.hello.server;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.requestfactory.shared.Receiver;

import demo.hello.shared.FilterConstraintOpEnum;
import demo.hello.shared.FilterConstraintProxy;
import demo.hello.shared.FilterConstraintTypeEnum;
import demo.hello.shared.ProjectRequestFactory;
import demo.hello.shared.ProjectRequestFactory.CellRequest;
import demo.hello.shared.SortConstraintProxy;
import demo.hello.shared.cell.CellProxy;

public class CellClient2 {
    protected final ProjectRequestFactory requestFactory;

    @Inject
    CellClient2(final Provider<ProjectRequestFactory> projectRequestFactory) {
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
     * newCellProxy
     */
    public CellProxy newCellProxy(final String a, final double b, final boolean c, final Date d,
        final CellRequest cellRequest) {
        // Logger.logpf(Level.INFO, "CellClient2", "newCellProxy", "a: %s, b: %d, c: %s, d: %tT", a,
        // b, c, d);

        final CellProxy cellProxy = cellRequest.create(CellProxy.class);
        cellProxy.setA(a);
        cellProxy.setB(b);
        cellProxy.setC(c);
        cellProxy.setD(d);

        return cellProxy;
    }

    /**
     * editCellProxy
     */
    public CellProxy editCellProxy(final CellProxy cellProxy, final CellRequest cellRequest) {
        // Logger.logp(Level.INFO, "CellClient2", "editCellProxy", "");

        final CellProxy edit = cellRequest.edit(cellProxy);
        return edit;
    }

    /**
     * editCellProxy
     */
    public CellProxy editCellProxy(final String a, final double b, final boolean c, final Date d,
        final CellProxy cellProxy, final CellRequest cellRequest) {
        // Logger.logpf(Level.INFO, "CellClient2", "editCellProxy", "a: %s, b: %d, c: %s, d: %tT", a,
        // b, c, d);

        final CellProxy edit = cellRequest.edit(cellProxy);
        edit.setA(a);
        edit.setB(b);
        edit.setC(c);
        edit.setD(d);

        return edit;
    }

    /**
     * createCell. fire the cellRequest
     */
    public void createCell(final String a, final double b, final boolean c, final Date d,
        final CellRequest cellRequest, final Receiver<CellProxy> receiver) {
        // Logger.logpf(Level.INFO, "CellClient2", "createCell", "a: %s, b: %d, c: %s, d: %tT", a, b,
        // c, d);

        final CellProxy cellProxy = cellRequest.create(CellProxy.class);
        cellProxy.setA(a);
        cellProxy.setB(b);
        cellProxy.setC(c);
        cellProxy.setD(d);

        cellRequest.createCell(cellProxy).fire(receiver);
    }

    /**
     * createCell. fire the cellRequest
     */
    public void createCell(final CellProxy cellProxy, final CellRequest cellRequest,
        final Receiver<CellProxy> receiver) {
        // Logger.logp(Level.INFO, "CellClient2", "createCell", cellProxy.toString());
        cellRequest.createCell(cellProxy).fire(receiver);
    }

    /**
     * updateCell. fire the cellRequest
     */
    public void updateCell(final CellProxy cellProxy, final CellRequest cellRequest,
        final Receiver<CellProxy> receiver) {
        // Logger.logp(Level.INFO, "CellClient2", "updateCell", cellProxy.toString());
        cellRequest.updateCell(cellProxy).fire(receiver);
    }

    /**
     * countCell
     */
    public void countCell(final Receiver<Long> receiver) {
        // Logger.logp(Level.INFO, "CellClient2", "countCell");
        requestFactory.cellRequest().countCell().fire(receiver);
    }

    /**
     * countCell
     */
    public void countCell(final Set<FilterConstraintProxy> filterConstraints,
        final Receiver<Long> receiver) {
        // Logger.logp(Level.INFO, "CellClient2", "countCell", filterToString(filterConstraints));

        final CellRequest cellRequest = requestFactory.cellRequest();
        cellRequest.countCell(filterConstraints).fire(receiver);

    }

    /**
     * listCell
     */
    public void listCell(final Receiver<List<CellProxy>> receiver) {
        // Logger.logp(Level.INFO, "CellClient2", "listCell");
        requestFactory.cellRequest().listCell().fire(receiver);
    }

    /**
     * listCell. fire the cellRequest
     */
    public void listCell(final int start, final int end,
        final Set<FilterConstraintProxy> filterConstraints,
        final Set<SortConstraintProxy> sortConstraints, final CellRequest cellRequest,
        final Receiver<List<CellProxy>> listReceiver) {

        listCellAndCount(start, end, filterConstraints, sortConstraints, cellRequest, listReceiver,
            null);
    }

    public void listCellAndCount(final int start, final int end,
        final Set<FilterConstraintProxy> filterConstraints,
        final Set<SortConstraintProxy> sortConstraints, final CellRequest cellRequest,
        final Receiver<List<CellProxy>> listReceiver, final Receiver<Long> countReceiver) {

        // Logger.logp(Level.INFO, "CellClient", "listCellAndCount", "start: " + start, "end: " + end,
        // "filterConstraints: " + filterToString(filterConstraints), "sortConstraints: "
        // + sortToString(sortConstraints));

        if (countReceiver != null) {
            cellRequest.countCell(filterConstraints).to(countReceiver);
        }

        cellRequest.listCell(start, end, filterConstraints, sortConstraints).to(listReceiver);
        cellRequest.fire();
    }

    /**
     * clear
     */
    public void clear() {
        requestFactory.cellRequest().clear().fire();
    }

}
