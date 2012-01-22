package demo.hello.server;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

import demo.hello.GuiceJUnit4Runner;
import demo.hello.server.cell.CellManager;
import demo.hello.shared.FilterConstraint;
import demo.hello.shared.FilterConstraintOpEnum;
import demo.hello.shared.FilterConstraintTypeEnum;
import demo.hello.shared.SortConstraint;
import demo.hello.shared.cell.CellEntity;

/**
 * CellManagerTest
 */
@RunWith(GuiceJUnit4Runner.class)
public class CellManagerTest {

    final CellManager cellManager;

    @Inject
    public CellManagerTest(final CellManager cellManager) {
        this.cellManager = cellManager;
    }

    @Before
    public void before() {
        cellManager.clear();
    }

    @Test
    public void createSelectUpdateDelete() {
        // create Cell
        final CellEntity a1 = createCell("A1", 1.11, true, new Date(1));

        // select Cell
        final Set<FilterConstraint> filterConstraints = new LinkedHashSet<FilterConstraint>();
        filterConstraints.add(new FilterConstraint("a", "A1", FilterConstraintTypeEnum.STRING));
        List<CellEntity> cellEntities = cellManager.listCell(filterConstraints);

        Assert.assertNotNull("cellEntities is null !", cellEntities);
        Assert.assertEquals(1, cellEntities.size());
        final CellEntity cellEntity = cellEntities.get(0);
        Assert.assertEquals(a1, cellEntity);

        // update Cell
        cellEntity.setC(false);
        final CellEntity updateCell = cellManager.updateCell(cellEntity);
        Assert.assertEquals(a1, updateCell);

        cellEntities = cellManager.listCell(filterConstraints);
        Assert.assertEquals(1, cellEntities.size());
        Assert.assertEquals(a1, cellEntities.get(0));

        // delete Cell
        cellManager.deleteCell(updateCell);
        final Long countCell = cellManager.countCell();
        Assert.assertEquals(0, countCell.longValue());
    }

    @Test
    public void sortAndPaginateCell() {
        final CellEntity a1 = createCell("A1", 1.11, true, new Date(1));
        final CellEntity b1 = createCell("B1", 2.11, false, new Date(2));
        final CellEntity c1 = createCell("C1", 3.11, true, new Date(3));
        final CellEntity d1 = createCell("D1", 4.11, false, new Date(4));

        // list Cell order by D asc, start=0, end=2
        int start = 0;
        int end = 2;
        Set<FilterConstraint> filterConstraints = new LinkedHashSet<FilterConstraint>();
        Set<SortConstraint> sortConstraints = new LinkedHashSet<SortConstraint>();
        sortConstraints.add(new SortConstraint("d", true));

        List<CellEntity> cellEntities = cellManager.listCell(start, end, filterConstraints,
            sortConstraints);

        Assert.assertNotNull("cellEntities is null !", cellEntities);
        Assert.assertEquals(2, cellEntities.size());
        Assert.assertEquals(a1, cellEntities.get(0));
        Assert.assertEquals(b1, cellEntities.get(1));

        // list Cell order by D asc, start=2, end=4
        start = 2;
        end = 4;
        filterConstraints = new LinkedHashSet<FilterConstraint>();
        sortConstraints = new LinkedHashSet<SortConstraint>();
        sortConstraints.add(new SortConstraint("d", true));

        cellEntities = cellManager.listCell(start, end, filterConstraints, sortConstraints);

        Assert.assertNotNull("cellEntities is null !", cellEntities);
        Assert.assertEquals(2, cellEntities.size());
        Assert.assertEquals(c1, cellEntities.get(0));
        Assert.assertEquals(d1, cellEntities.get(1));

        // list Cell order by C asc, D desc, start=0, end=2
        start = 0;
        end = 2;
        filterConstraints = new LinkedHashSet<FilterConstraint>();
        sortConstraints = new LinkedHashSet<SortConstraint>();
        sortConstraints.add(new SortConstraint("c", true));
        sortConstraints.add(new SortConstraint("d", false));

        cellEntities = cellManager.listCell(start, end, filterConstraints, sortConstraints);

        Assert.assertNotNull("cellEntities is null !", cellEntities);
        Assert.assertEquals(2, cellEntities.size());
        Assert.assertEquals(d1, cellEntities.get(0));
        Assert.assertEquals(b1, cellEntities.get(1));
    }

    @Test
    public void filterCell() {
        final CellEntity a1 = createCell("A1", 1.11, true, new Date(1));
        createCell("B1", 2.11, false, new Date(2));
        final CellEntity c1 = createCell("C1", 3.11, true, new Date(3));
        createCell("D1", 4.11, false, new Date(4));

        // list Cell filter C=true, order by D asc, start=0, end=2
        Set<FilterConstraint> filterConstraints = new LinkedHashSet<FilterConstraint>();
        filterConstraints.add(new FilterConstraint("c", "true", FilterConstraintTypeEnum.BOOLEAN));
        Set<SortConstraint> sortConstraints = new LinkedHashSet<SortConstraint>();
        sortConstraints.add(new SortConstraint("d", true));

        List<CellEntity> cellEntities = cellManager.listCell(filterConstraints, sortConstraints);

        Assert.assertNotNull("cellEntities is null !", cellEntities);
        Assert.assertEquals(2, cellEntities.size());
        Assert.assertEquals(a1, cellEntities.get(0));
        Assert.assertEquals(c1, cellEntities.get(1));

        // list Cell filter C=true and D=3, order by D asc, start=0, end=2
        filterConstraints = new LinkedHashSet<FilterConstraint>();
        filterConstraints.add(new FilterConstraint("c", "true", FilterConstraintTypeEnum.BOOLEAN));
        filterConstraints.add(new FilterConstraint("d", "3", FilterConstraintTypeEnum.DATE));
        sortConstraints = new LinkedHashSet<SortConstraint>();
        sortConstraints.add(new SortConstraint("d", true));

        cellEntities = cellManager.listCell(filterConstraints, sortConstraints);

        Assert.assertNotNull("cellEntities is null !", cellEntities);
        Assert.assertEquals(1, cellEntities.size());
        Assert.assertEquals(c1, cellEntities.get(0));

        // list Cell filter C=true and D=3 and A=C1, order by D asc, start=0, end=2
        filterConstraints = new LinkedHashSet<FilterConstraint>();
        filterConstraints.add(new FilterConstraint("c", "true", FilterConstraintTypeEnum.BOOLEAN));
        filterConstraints.add(new FilterConstraint("d", "3", FilterConstraintTypeEnum.DATE));
        filterConstraints.add(new FilterConstraint("a", "C1", FilterConstraintTypeEnum.STRING));
        sortConstraints = new LinkedHashSet<SortConstraint>();
        sortConstraints.add(new SortConstraint("d", true));

        cellEntities = cellManager.listCell(filterConstraints, sortConstraints);

        Assert.assertNotNull("cellEntities is null !", cellEntities);
        Assert.assertEquals(1, cellEntities.size());
        Assert.assertEquals(c1, cellEntities.get(0));
    }

    @Test
    public void filterCellWithOp() {
        final CellEntity a1 = createCell("A1", 1.11, true, new Date(1));
        final CellEntity b1 = createCell("B1", 2.11, false, new Date(2));
        final CellEntity c1 = createCell("C1", 3.11, true, new Date(3));
        final CellEntity d1 = createCell("D1", 4.11, false, new Date(4));
        final CellEntity e1 = createCell("Eabcde", 5.11, false, new Date(5));
        final CellEntity e2 = createCell("Efghij", 5.11, false, new Date(5));
        final CellEntity f1 = createCell("F1", 6.11, false, new Date(6));

        // list Cell filter B ge 2.1, order by D asc, start=0, end=2
        final int start = 0;
        final int end = 2;
        Set<FilterConstraint> filterConstraints = new LinkedHashSet<FilterConstraint>();
        filterConstraints.add(new FilterConstraint("b", "2.1", FilterConstraintTypeEnum.FLOAT,
            FilterConstraintOpEnum.GE));
        Set<SortConstraint> sortConstraints = new LinkedHashSet<SortConstraint>();
        sortConstraints.add(new SortConstraint("d", true));

        List<CellEntity> cellEntities = cellManager.listCell(start, end, filterConstraints,
            sortConstraints);

        Assert.assertNotNull("cellEntities is null !", cellEntities);
        Assert.assertEquals(2, cellEntities.size());
        Assert.assertEquals(b1, cellEntities.get(0));
        Assert.assertEquals(c1, cellEntities.get(1));

        // list Cell filter A ge E, order by A asc, start=0, end=3
        filterConstraints = new LinkedHashSet<FilterConstraint>();
        filterConstraints.add(new FilterConstraint("a", "E", FilterConstraintTypeEnum.STRING,
            FilterConstraintOpEnum.GE));
        sortConstraints = new LinkedHashSet<SortConstraint>();
        sortConstraints.add(new SortConstraint("a", true));

        cellEntities = cellManager.listCell(filterConstraints, sortConstraints);

        Assert.assertNotNull("cellEntities is null !", cellEntities);
        Assert.assertEquals(3, cellEntities.size());
        Assert.assertEquals(e1, cellEntities.get(0));
        Assert.assertEquals(e2, cellEntities.get(1));
        Assert.assertEquals(f1, cellEntities.get(2));

        // list Cell filter A sw E, order by A asc, start=0, end=3
        filterConstraints = new LinkedHashSet<FilterConstraint>();
        filterConstraints.add(new FilterConstraint("a", "E", FilterConstraintTypeEnum.STRING,
            FilterConstraintOpEnum.SW));
        sortConstraints = new LinkedHashSet<SortConstraint>();
        sortConstraints.add(new SortConstraint("a", true));

        cellEntities = cellManager.listCell(filterConstraints, sortConstraints);

        Assert.assertNotNull("cellEntities is null !", cellEntities);
        Assert.assertEquals(2, cellEntities.size());
        Assert.assertEquals(e1, cellEntities.get(0));
        Assert.assertEquals(e2, cellEntities.get(1));

        // list Cell filter A ew 1, order by A asc, start=0, end=10
        filterConstraints = new LinkedHashSet<FilterConstraint>();
        filterConstraints.add(new FilterConstraint("a", "1", FilterConstraintTypeEnum.STRING,
            FilterConstraintOpEnum.EW));
        sortConstraints = new LinkedHashSet<SortConstraint>();
        sortConstraints.add(new SortConstraint("a", true));

        cellEntities = cellManager.listCell(filterConstraints, sortConstraints);

        Assert.assertNotNull("cellEntities is null !", cellEntities);
        Assert.assertEquals(5, cellEntities.size());
        Assert.assertEquals(a1, cellEntities.get(0));
        Assert.assertEquals(b1, cellEntities.get(1));
        Assert.assertEquals(c1, cellEntities.get(2));
        Assert.assertEquals(d1, cellEntities.get(3));
        Assert.assertEquals(f1, cellEntities.get(4));
    }

    @Test
    public void validate() {
        tryCreateCell(new CellEntity("A1", 1.11, true, null));
        tryCreateCell(new CellEntity("A1", 1.11, (Boolean) null, new Date(1)));
        tryCreateCell(new CellEntity("A1", (Double) null, true, new Date(1)));
        tryCreateCell(new CellEntity(null, 1.11, true, new Date(1)));
    }

    void tryCreateCell(final CellEntity cellEntity) {
        try {
            cellManager.createCell(cellEntity);
            Assert.fail("ConstraintViolationException expected");
        } catch (final Exception e) {
            Assert.assertTrue(e instanceof ConstraintViolationException);
        }
    }

    /**
     * createCell
     */
    CellEntity createCell(final String a, final Double b, final Boolean c, final Date d) {
        final CellEntity cellEntity = new CellEntity(a, b, c, d);
        return cellManager.createCell(cellEntity);
    }
}
