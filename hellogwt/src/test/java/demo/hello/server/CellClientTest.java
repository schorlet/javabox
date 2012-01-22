package demo.hello.server;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import demo.hello.GuiceJUnit4Runner;
import demo.hello.shared.FilterConstraintProxy;
import demo.hello.shared.FilterConstraintTypeEnum;
import demo.hello.shared.ProjectRequestFactory.CellRequest;
import demo.hello.shared.SortConstraintProxy;
import demo.hello.shared.cell.CellProxy;

/**
 * CellClientTest
 */
@RunWith(GuiceJUnit4Runner.class)
public class CellClientTest {

    final CellClient2 cellClient;

    @Inject
    public CellClientTest(final CellClient2 cellClient) {
        this.cellClient = cellClient;
    }

    @Before
    public void before() {
        cellClient.clear();
    }

    @Test
    public void listCell() throws Exception {
        // create Cell
        final ReceiverCellProxy receiverCellProxy = new ReceiverCellProxy();
        cellClient.createCell("A1", 1.1, true, new Date(1), cellClient.newCellRequest(),
            receiverCellProxy);
        assertCellProxy("A1", 1.1, true, new Date(1), receiverCellProxy.getCell());

        // list Cell
        final ReceiverListCellProxy receiverListCellProxy = new ReceiverListCellProxy();
        cellClient.listCell(receiverListCellProxy);

        final List<CellProxy> cellProxies = receiverListCellProxy.getCells();
        Assert.assertNotNull("cellProxies is null !", cellProxies);
        Assert.assertEquals(1, cellProxies.size());

        final CellProxy cellProxy2 = cellProxies.get(0);
        assertCellProxy("A1", 1.1, true, new Date(1), cellProxy2);
    }

    @Test
    public void updateCell() throws Exception {
        final ReceiverCellProxy receiverCellProxy = new ReceiverCellProxy();

        // create Cell
        final CellRequest newCellRequest = cellClient.newCellRequest();
        final CellProxy cellProxy = cellClient.newCellProxy("B1", 1.1, true, new Date(1),
            newCellRequest);

        cellClient.createCell(cellProxy, newCellRequest, receiverCellProxy);
        assertCellProxy("B1", 1.1, true, new Date(1), receiverCellProxy.getCell());

        // update Cell
        final CellRequest newCellRequest2 = cellClient.newCellRequest();
        final CellProxy cellProxy2 = cellClient.editCellProxy("C1", 2.2, false, new Date(2),
            receiverCellProxy.getCell(), newCellRequest2);

        cellClient.updateCell(cellProxy2, newCellRequest2, receiverCellProxy);
        assertCellProxy("C1", 2.2, false, new Date(2), receiverCellProxy.getCell());

        // update Cell
        final CellRequest newCellRequest3 = cellClient.newCellRequest();
        final CellProxy cellProxy3 = cellClient.editCellProxy(receiverCellProxy.getCell(),
            newCellRequest3);

        cellProxy3.setA("D1");
        cellProxy3.setB(3.3);
        cellProxy3.setC(true);
        cellProxy3.setD(new Date(3));

        cellClient.updateCell(cellProxy3, newCellRequest3, receiverCellProxy);
        assertCellProxy("D1", 3.3, true, new Date(3), receiverCellProxy.getCell());
    }

    @Test
    public void sortAndPaginateCell() {
        // create Cells
        final ReceiverCellProxy receiverCellProxy = new ReceiverCellProxy();
        cellClient.createCell("A1", 1.1, true, new Date(1), cellClient.newCellRequest(),
            receiverCellProxy);
        cellClient.createCell("B1", 2.1, false, new Date(2), cellClient.newCellRequest(),
            receiverCellProxy);
        cellClient.createCell("C1", 3.1, true, new Date(3), cellClient.newCellRequest(),
            receiverCellProxy);
        cellClient.createCell("D1", 4.1, false, new Date(4), cellClient.newCellRequest(),
            receiverCellProxy);

        // list Cell order by D asc, start=0, end=2
        ReceiverListCellProxy receiverListCellProxy = new ReceiverListCellProxy();
        CellRequest newCellRequest = cellClient.newCellRequest();

        int start = 0;
        int end = 2;
        Set<FilterConstraintProxy> filterConstraints = new LinkedHashSet<FilterConstraintProxy>();
        Set<SortConstraintProxy> sortConstraints = new LinkedHashSet<SortConstraintProxy>();
        sortConstraints.add(cellClient.newSortConstraintProxy("d", true, newCellRequest));

        cellClient.listCell(start, end, filterConstraints, sortConstraints, newCellRequest,
            receiverListCellProxy);

        List<CellProxy> cellProxies = receiverListCellProxy.getCells();
        Assert.assertNotNull("cellProxies is null !", cellProxies);
        Assert.assertEquals(2, cellProxies.size());

        assertCellProxy("A1", 1.1, true, new Date(1), cellProxies.get(0));
        assertCellProxy("B1", 2.1, false, new Date(2), cellProxies.get(1));

        // list Cell order by D asc, start=2, end=4
        receiverListCellProxy = new ReceiverListCellProxy();
        newCellRequest = cellClient.newCellRequest();

        start = 2;
        end = 4;
        filterConstraints = new LinkedHashSet<FilterConstraintProxy>();
        sortConstraints = new LinkedHashSet<SortConstraintProxy>();
        sortConstraints.add(cellClient.newSortConstraintProxy("d", true, newCellRequest));

        cellClient.listCell(start, end, filterConstraints, sortConstraints, newCellRequest,
            receiverListCellProxy);

        cellProxies = receiverListCellProxy.getCells();
        Assert.assertNotNull("cellProxies is null !", cellProxies);
        Assert.assertEquals(2, cellProxies.size());

        assertCellProxy("C1", 3.1, true, new Date(3), cellProxies.get(0));
        assertCellProxy("D1", 4.1, false, new Date(4), cellProxies.get(1));

        // FIXME later, see issue 6354
        // list Cell order by C asc, D desc, start=0, end=2
        // receiverListCellProxy = new ReceiverListCellProxy();
        // newCellRequest = cellClient.newCellRequest();
        //
        // start = 0;
        // end = 2;
        // filterConstraints = new LinkedHashSet<FilterConstraintProxy>();
        // sortConstraints = new LinkedHashSet<SortConstraintProxy>();
        // sortConstraints.add(cellClient.newSortConstraintProxy("c", true, newCellRequest));
        // sortConstraints.add(cellClient.newSortConstraintProxy("d", false, newCellRequest));
        //
        // cellClient.listCell(start, end, filterConstraints, sortConstraints, newCellRequest,
        // receiverListCellProxy);
        //
        // cellProxies = receiverListCellProxy.getCells();
        // Assert.assertNotNull("cellProxies is null !", cellProxies);
        // Assert.assertEquals(2, cellProxies.size());
        //
        // assertCellProxy("D1", 4.1, false, new Date(4), cellProxies.get(0));
        // assertCellProxy("B1", 2.1, false, new Date(2), cellProxies.get(1));
    }

    @Test
    public void filterCell() {
        // create Cells
        final ReceiverCellProxy receiverCellProxy = new ReceiverCellProxy();
        cellClient.createCell("A1", 1.1, true, new Date(1), cellClient.newCellRequest(),
            receiverCellProxy);
        cellClient.createCell("B1", 2.1, false, new Date(2), cellClient.newCellRequest(),
            receiverCellProxy);
        cellClient.createCell("C1", 3.1, true, new Date(3), cellClient.newCellRequest(),
            receiverCellProxy);
        cellClient.createCell("D1", 4.1, false, new Date(4), cellClient.newCellRequest(),
            receiverCellProxy);

        // list Cell filter C=true, order by D asc, start=0, end=2
        final ReceiverListCellProxy receiverListCellProxy = new ReceiverListCellProxy();
        final CellRequest newCellRequest = cellClient.newCellRequest();

        final int start = 0;
        final int end = 2;
        final Set<FilterConstraintProxy> filterConstraints = new LinkedHashSet<FilterConstraintProxy>();
        filterConstraints.add(cellClient.newFilterConstraintProxy("c", "true",
            FilterConstraintTypeEnum.BOOLEAN, newCellRequest));
        final Set<SortConstraintProxy> sortConstraints = new LinkedHashSet<SortConstraintProxy>();
        sortConstraints.add(cellClient.newSortConstraintProxy("d", true, newCellRequest));

        cellClient.listCell(start, end, filterConstraints, sortConstraints, newCellRequest,
            receiverListCellProxy);

        final List<CellProxy> cellProxies = receiverListCellProxy.getCells();
        Assert.assertNotNull("cellProxies is null !", cellProxies);
        Assert.assertEquals(2, cellProxies.size());

        assertCellProxy("A1", 1.1, true, new Date(1), cellProxies.get(0));
        assertCellProxy("C1", 3.1, true, new Date(3), cellProxies.get(1));

        // FIXME later, see issue 6354
        // list Cell filter C=true and D=3, order by D asc, start=0, end=2
        // receiverListCellProxy = new ReceiverListCellProxy();
        // newCellRequest = cellClient.newCellRequest();
        //
        // start = 0;
        // end = 2;
        // filterConstraints = new LinkedHashSet<FilterConstraintProxy>();
        // filterConstraints.add(cellClient.newFilterConstraintProxy("c", "true",
        // FilterConstraintTypeEnum.BOOLEAN, newCellRequest));
        // filterConstraints.add(cellClient.newFilterConstraintProxy("d", "3",
        // FilterConstraintTypeEnum.DATE, newCellRequest));
        // sortConstraints = new LinkedHashSet<SortConstraintProxy>();
        // sortConstraints.add(cellClient.newSortConstraintProxy("d", true, newCellRequest));
        //
        // cellClient.listCell(start, end, filterConstraints, sortConstraints, newCellRequest,
        // receiverListCellProxy);
        //
        // cellProxies = receiverListCellProxy.getCells();
        // Assert.assertNotNull("cellProxies is null !", cellProxies);
        // Assert.assertEquals(1, cellProxies.size());
        //
        // assertCellProxy("C1", 3.1, true, new Date(3), cellProxies.get(0));
    }

    /**
     * assertCellProxy
     */
    void assertCellProxy(final String a, final Double b, final Boolean c, final Date d,
        final CellProxy cellProxy) {
        Assert.assertNotNull("cell id is null !", cellProxy.getId());
        Assert.assertEquals(a, cellProxy.getA());
        Assert.assertEquals(b, cellProxy.getB());
        Assert.assertEquals(c, cellProxy.getC());
        Assert.assertEquals(d.getTime(), cellProxy.getD().getTime());
    }

    /**
     * ReceiverCellProxy
     */
    class ReceiverCellProxy extends Receiver<CellProxy> {
        CellProxy cellProxy;

        @Override
        public void onSuccess(final CellProxy response) {
            System.err.println(response);
            cellProxy = response;
        }

        @Override
        public void onFailure(final ServerFailure error) {
            Assert.fail(error.getStackTraceString());
        }

        public CellProxy getCell() {
            return cellProxy;
        }
    }

    /**
     * ReceiverListCellProxy
     */
    class ReceiverListCellProxy extends Receiver<List<CellProxy>> {
        List<CellProxy> cellProxies = Collections.emptyList();

        public ReceiverListCellProxy() {}

        @Override
        public void onSuccess(final List<CellProxy> response) {
            System.err.println(response);
            cellProxies = response;
        }

        @Override
        public void onFailure(final ServerFailure error) {
            Assert.fail(error.getStackTraceString());
        }

        public List<CellProxy> getCells() {
            return Collections.unmodifiableList(cellProxies);
        }
    }

}
