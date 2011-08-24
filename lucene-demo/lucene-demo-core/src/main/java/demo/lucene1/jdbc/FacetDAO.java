package demo.lucene1.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import demo.lucene1.Commons;

/**
 * FacetDAO
 *
 * @author sch
 */
public class FacetDAO extends JDBCSupport {
    final Logger logger = LoggerFactory.getLogger(getClass());

    static {
        try {
            Class.forName(Commons.getJDBCDriver());
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public FacetDAO(final String url) {
        super(url);
    }

    public FacetDAO() {
        this(Commons.getJDBCUrl());
    }

    public void saveFacetMap(final Map<String, Set<String>> facetMap) throws SQLException {
        Transaction transaction = null;

        try {
            transaction = beginTransaction();
            final Connection connection = transaction.connection;

            execute("delete from facets", transaction);

            final PreparedStatement ps = connection.prepareStatement(
                "insert into facets(facet_name, facet_value) values (?, ?)",
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

            for (final String facetName : facetMap.keySet()) {
                final Set<String> facetValues = facetMap.get(facetName);

                for (final String facetValue : facetValues) {
                    logger.info("  insert into facets(facet_name, facet_value) values ({}, {})",
                        facetName, facetValue);

                    ps.setString(1, facetName);
                    ps.setString(2, facetValue);
                    ps.addBatch();
                }
            }

            ps.executeBatch();
            ps.close();

            transaction.commit();

        } catch (final SQLException e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    /**
     * Select all facet names and values.
     *
     * @return a map of facet values indexed by names
     */
    public Map<String, Set<String>> getAllFacets() {
        final Map<String, Set<String>> facetList = new HashMap<String, Set<String>>();

        select(
            "select distinct facet_name, facet_value from facets order by facet_name, facet_value",
            new SelectCallback() {
                @Override
                public void success(final ResultSet resultSet) throws SQLException {
                    while (resultSet.next()) {
                        String facetName;
                        facetName = resultSet.getString("facet_name");
                        final String facetValue = resultSet.getString("facet_value");

                        Set<String> facetValues = facetList.get(facetName);
                        if (facetValues == null) {
                            facetValues = new HashSet<String>();
                            facetList.put(facetName, facetValues);
                        }

                        facetValues.add(facetValue);
                    }
                }

                @Override
                public void error(final RuntimeException runtimeException) {
                    throw runtimeException;
                }
            });

        return facetList;
    }

    /**
     * Select all facet names.
     *
     * @return a list of facet names
     */
    public Set<String> getAllFacetNames() {
        final Set<String> facetNames = new HashSet<String>();

        select("select distinct facet_name from facets order by facet_name", new SelectCallback() {
            @Override
            public void success(final ResultSet resultSet) {
                try {
                    while (resultSet.next()) {
                        facetNames.add(resultSet.getString("facet_name"));
                    }
                } catch (final SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void error(final RuntimeException runtimeException) {
                throw runtimeException;
            }
        });

        return facetNames;
    }

    /**
     * Select all facet values.
     *
     * @return a list of facet values
     */
    public Set<String> getAllFacetValues(final String facetName) {
        final Set<String> facetValues = new HashSet<String>();

        select("select facet_value from facets where facet_name = ? order by facet_value",
            new String[] { facetName }, new SelectCallback() {
                @Override
                public void success(final ResultSet resultSet) {
                    try {
                        while (resultSet.next()) {
                            facetValues.add(resultSet.getString("facet_value"));
                        }
                    } catch (final SQLException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void error(final RuntimeException runtimeException) {
                    throw runtimeException;
                }
            });

        return facetValues;
    }

    /**
     * Creates the table: {@literal facets (facet_name, facet_value)}
     * @throws SQLException
     * @throws Exception
     */
    public void createTable() throws SQLException {
        Transaction transaction = null;

        try {
            transaction = beginTransaction();
            execute("drop table if exists facets", transaction);
            execute("create table facets (facet_name, facet_value)", transaction);
            transaction.commit();

        } catch (final SQLException e) {
            try {
                if (transaction != null) {
                    transaction.rollback();
                }
            } catch (final SQLException e1) {
                e.printStackTrace();
            }

            throw e;
        }
    }

}
