package demo.gap.server.mongo;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.commons.lang.UnhandledException;
import org.jongo.Find;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

import demo.gap.shared.domain.pojo.Gap;
import demo.gap.shared.domain.service.Filter;
import demo.gap.shared.domain.service.GapService;

/**
 * MongoGapService
 */
public class MongoGapService implements GapService {
    final Logger logger = LoggerFactory.getLogger(MongoGapService.class);

    final Provider<Jongo> jongo;

    @Inject
    MongoGapService(final Provider<Jongo> provider) {
        this.jongo = provider;
    }

    MongoCollection getCollection() {
        return jongo.get().getCollection("gaps");
    }

    @Override
    public boolean isEmpty() {
        logger.trace("isEmpty");
        return size() == 0;
    }

    @Override
    public int size() {
        logger.trace("size");

        final long count = getCollection().count("{}");
        return (int) count;
    }

    @Override
    public Set<String> getVersions() {
        logger.trace("getVersions");

        final Set<String> versions = new HashSet<String>();

        final Iterable<String> distinct = getCollection().distinct("version", "{}", String.class);
        for (final String version : distinct) {
            versions.add(version);
        }

        return versions;
    }

    @Override
    public Set<Gap> getGaps() {
        return getByFilter(new Filter());
    }

    @Override
    public Gap getById(final String id) {
        logger.trace("getById {}", id);

        final Set<Gap> gaps = getByFilter(new Filter().byGapId(id));

        if (gaps.isEmpty()) return null;
        else return gaps.iterator().next();
    }

    @Override
    public Set<Gap> getByFilter(final Filter filter) {
        logger.trace("getByFilter {}", filter);

        final LinkedHashMap<String, Object> params = new LinkedHashMap<String, Object>();

        // where
        if (filter.getGapId() != null) {
            params.put("id: #", filter.getGapId());

        }
        if (filter.getVersion() != null) {
            params.put("version: #", filter.getVersion());
        }

        final MongoCollection collection = getCollection();
        final Find find = collection.find(JongoDomainUtil.toQueryString(params.keySet()),
            params.values().toArray()).sort("{version: 1}");

        // create query from criteria query
        final Iterable<GapEntity> gaps = find.as(GapEntity.class);
        return JongoDomainUtil.buildGaps(gaps);
    }

    @Override
    public void addAll(final Set<Gap> gaps) {
        logger.trace("addAll {}", gaps);

        for (final Gap gap : gaps) {
            add(gap);
        }
    }

    @Override
    public void add(final Gap gap) {
        logger.trace("add {}", gap);

        final MongoCollection collection = getCollection();

        try {
            GapEntity entity = collection.findOne("{id: #}", gap.getId()).as(GapEntity.class);

            if (entity == null) {
                entity = new GapEntity(gap.getId(), gap.getVersion(), gap.getDescription());
                collection.save(entity);

            } else {
                final String query = String.format("{id: '%s'}", gap.getId());
                final String modifier = String.format(
                    "{$set : {version: '%s', description: '%s'}}", gap.getVersion(),
                    gap.getDescription());

                collection.update(query, modifier);
            }

        } catch (final IOException e) {
            throw new UnhandledException(e);
        }
    }

    @Override
    public boolean remove(final Gap gap) {
        final MongoCollection collection = getCollection();
        final GapEntity entity = JongoDomainUtil.buildGapEntity(gap, collection);

        if (entity != null) {
            logger.trace("remove {}", gap.getId());
            String query = String.format("{id: '%s'}", gap.getId());
            collection.remove(query);

            // remove activities
            query = String.format("{gap.id: '%s'}", gap.getId());
            jongo.get().getCollection("activities").remove(query);

            return true;

        } else {
            logger.warn("remove {} does not exists", gap.getId());
            return false;
        }
    }

    @Override
    public void clear() {
        logger.trace("clear");
        getCollection().drop();

        // drop activities
        jongo.get().getCollection("activities").drop();
    }

}
