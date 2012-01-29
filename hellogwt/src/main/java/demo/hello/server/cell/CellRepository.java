package demo.hello.server.cell;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;

import demo.hello.server.SuperRepository;
import demo.hello.shared.cell.CellEntity;

/**
 * CellRepository
 */
public class CellRepository extends SuperRepository<CellEntity> {

    @Inject
    CellRepository(final Provider<EntityManager> provider) {
        super(provider);
    }

}
