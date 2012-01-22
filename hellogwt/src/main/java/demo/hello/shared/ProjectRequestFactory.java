package demo.hello.shared;

import java.util.List;
import java.util.Set;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.RequestFactory;
import com.google.web.bindery.requestfactory.shared.Service;

import demo.hello.server.cell.CellManager;
import demo.hello.server.plumbing.ProjectServiceLocator;
import demo.hello.shared.cell.CellProxy;

/**
 * ProjectRequestFactory.
 * 
 * RequestFactory service endpoints.
 */
public interface ProjectRequestFactory extends RequestFactory {
    /**
     * @return CellRequest
     */
    CellRequest cellRequest();

    /**
     * CellRequest
     */
    @Service(value = CellManager.class, locator = ProjectServiceLocator.class)
    public interface CellRequest extends RequestContext {

        Request<Long> countCell();

        Request<Long> countCell(Set<FilterConstraintProxy> filterConstraints);

        Request<List<CellProxy>> listCell();

        Request<List<CellProxy>> listCell(int start, int end);

        Request<List<CellProxy>> listCell(int start, int end,
            Set<FilterConstraintProxy> filterConstraints, Set<SortConstraintProxy> sortConstraints);

        Request<CellProxy> createCell(CellProxy tacheProxy);

        Request<CellProxy> updateCell(CellProxy tacheProxy);

        Request<Void> clear();

        Request<Void> deleteCell(CellProxy tacheProxy);

        Request<CellProxy> findCell(Integer id);
    }
}
