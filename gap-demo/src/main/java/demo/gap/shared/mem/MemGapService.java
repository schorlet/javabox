package demo.gap.shared.mem;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.gap.shared.domain.pojo.Gap;
import demo.gap.shared.domain.service.Filter;
import demo.gap.shared.domain.service.GapService;

/**
 * MemGapService
 */
public class MemGapService implements GapService {
    static final Logger logger = LoggerFactory.getLogger(MemGapService.class);

    private final Set<Gap> gaps_ = new LinkedHashSet<Gap>();

    @Override
    public boolean isEmpty() {
        logger.trace("isEmpty");
        return gaps_.isEmpty();
    }

    @Override
    public int size() {
        logger.trace("size");
        return gaps_.size();
    }

    @Override
    public Set<String> getVersions() {
        logger.trace("getVersions");

        final Set<String> versions = new HashSet<String>();

        for (final Gap gap : gaps_) {
            versions.add(gap.getVersion());
        }

        return versions;
    }

    @Override
    public Set<Gap> getGaps() {
        logger.trace("{}", "getGaps");
        return new HashSet<Gap>(gaps_);
    }

    @Override
    public Gap getById(final String id) {
        logger.trace("getById {}", id);

        final Set<Gap> gaps = getByFilter(new Filter().byId(id));

        if (gaps.isEmpty()) return null;
        else {
            assert gaps.size() == 1 : "getById should return only one Gap";
            return gaps.iterator().next();
        }
    }

    @Override
    public Set<Gap> getByFilter(final Filter filter) {
        logger.trace("getByFilter {}", filter);

        final Set<Gap> copy = new LinkedHashSet<Gap>();

        final boolean idIsNotNull = Filter.notNull(filter.getId());
        final boolean versionIsNotNull = Filter.notNull(filter.getVersion());

        for (final Gap gap : gaps_) {
            final boolean add = filter.apply(gap);

            if (add && idIsNotNull) {
                copy.add(gap);
                break;

            } else if (add) {
                copy.add(gap);

            } else if (!idIsNotNull && !versionIsNotNull) {
                copy.add(gap);

            } else if (!idIsNotNull && versionIsNotNull) {
                if (Filter.equals(filter.getVersion(), gap.getVersion())) {
                    copy.add(gap);
                }
            }
        }

        return copy;
    }

    @Override
    public void clear() {
        logger.trace("clear");
        gaps_.clear();
    }

    @Override
    public boolean remove(final Gap gap) {
        logger.trace("remove {}", gap);
        return gaps_.remove(gap);
    }

    @Override
    public void addAll(final Set<Gap> gaps) {
        logger.trace("addAll {}", gaps);
        gaps_.addAll(gaps);
    }

    @Override
    public void add(final Gap gap) {
        logger.trace("add {}", gap);

        if (gaps_.contains(gap)) {
            final Gap byId = getById(gap.getId());
            byId.setVersion(gap.getVersion());
            byId.setDescription(gap.getDescription());

        } else {
            gap.clear();
            gaps_.add(gap);
        }
    }

}
