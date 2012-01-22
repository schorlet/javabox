package demo.hello.server.cell;

import javax.persistence.EntityManager;

import com.google.inject.Inject;

import demo.hello.server.SuperRepository;
import demo.hello.shared.cell.CellEntity;

/**
 * CellRepository
 */
public class CellRepository extends SuperRepository<CellEntity> {

    @Inject
    CellRepository(final EntityManager entityManager) {
        super(entityManager);
    }

}
