package demo.hello.server.cell;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

import demo.hello.shared.FilterConstraint;
import demo.hello.shared.SortConstraint;
import demo.hello.shared.cell.CellEntity;

/**
 * CellManager
 */
public class CellManager {

    final Logger logger = LoggerFactory.getLogger("demo.hello.server.cell.CellManager");

    final CellRepository cellRepository;
    final Validator validator;

    @Inject
    CellManager(final CellRepository tacheRepository, final ValidatorFactory validatorFactory) {
        this.cellRepository = tacheRepository;
        this.validator = validatorFactory.getValidator();
    }

    /**
     * @throws ConstraintViolationException
     */
    void validate(final CellEntity cell) throws ConstraintViolationException {
        final Set<ConstraintViolation<CellEntity>> constraintViolations = validator.validate(cell);
        if (constraintViolations.isEmpty()) return;

        throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(
            constraintViolations));
    }

    @Transactional
    public CellEntity createCell(final CellEntity cell) {
        logger.debug("create CellEntity {}", cell);
        validate(cell);
        cellRepository.persist(cell);
        return cell;
    }

    @Transactional
    public CellEntity updateCell(final CellEntity cell) {
        logger.debug("update CellEntity {}", cell);
        validate(cell);
        return cellRepository.merge(cell);
    }

    @Transactional
    public void deleteCell(final CellEntity cell) {
        logger.debug("delete CellEntity {}", cell);
        cellRepository.remove(cell);
    }

    @Transactional
    public void clear() {
        logger.debug("clear CellEntity");
        cellRepository.clear(CellEntity.class);
    }

    public Long countCell() {
        logger.debug("count CellEntity(): [");
        final Long count = cellRepository.count(CellEntity.class);
        logger.debug("  {}]", count);
        return count;
    }

    public Long countCell(final Set<FilterConstraint> filterConstraints) {
        logger.debug("count CellEntity (filterConstraints: {}): (", filterConstraints);
        final Long count = cellRepository.count(CellEntity.class, filterConstraints);
        logger.debug("  {})", count);
        return count;
    }

    public List<CellEntity> listCell() {
        logger.debug("list CellEntity");
        return cellRepository.list(CellEntity.class);
    }

    public List<CellEntity> listCell(final Set<FilterConstraint> filterConstraints) {
        logger.debug("find CellEntity {}", filterConstraints);
        return cellRepository.list(CellEntity.class, filterConstraints);
    }

    public List<CellEntity> listCell(final Set<FilterConstraint> filterConstraints,
        final Set<SortConstraint> sortConstraints) {

        logger.debug("list CellEntity (filterConstraints: {}, sortConstraints: {})", new Object[] {
            filterConstraints, sortConstraints });
        return cellRepository.list(CellEntity.class, filterConstraints, sortConstraints);
    }

    public List<CellEntity> listCell(final int start, final int end) {
        logger.debug("list CellEntity (start: {}, end: {})", start, end);
        return cellRepository.list(CellEntity.class, start, end);
    }

    public List<CellEntity> listCell(final int start, final int end,
        final Set<FilterConstraint> filterConstraints, final Set<SortConstraint> sortConstraints) {

        logger.debug(
            "list CellEntity (start: {}, end: {}, filterConstraints: {}, sortConstraints: {})",
            new Object[] { start, end, filterConstraints, sortConstraints });
        return cellRepository
            .list(CellEntity.class, start, end, filterConstraints, sortConstraints);
    }

    public CellEntity findCell(final Integer id) {
        logger.debug("find CellEntity {}", id);
        return (CellEntity) cellRepository.find(CellEntity.class, id);
    }
}
