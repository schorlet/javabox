package demo.lucene1.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JDBCSupport
 *
 * @author sch
 */
public abstract class JDBCSupport {
    final Logger logger = LoggerFactory.getLogger(getClass());

    final String url;

    /**
     * @param url
     */
    protected JDBCSupport(final String url) {
        this.url = url;
    }

    protected interface SelectCallback {
        void success(ResultSet resultSet) throws SQLException;

        void error(RuntimeException runtimeException);
    }

    protected void select(final String sql, final SelectCallback selectCallback) {
        select(sql, null, selectCallback);
    }

    protected void select(final String sql, final String[] params,
        final SelectCallback selectCallback) {
        Connection connection = null;
        try {
            connection = getAutoCommitConnection();
            connection.setReadOnly(true);

            logger.info(sql);
            final PreparedStatement ps = connection.prepareStatement(sql,
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setString(i + 1, params[i]);
                }
            }
            final ResultSet resultSet = ps.executeQuery();

            selectCallback.success(resultSet);
            ps.close();

        } catch (final Exception e) {
            selectCallback.error(new RuntimeException(e));

        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (final Exception e) {
                logger.error("connection.close(), {}", e);
            }
        }
    }

    protected int update(final String sql, final String[] params) throws SQLException {
        Connection connection = null;
        try {
            logger.info(sql);
            connection = getAutoCommitConnection();

            final PreparedStatement ps = connection.prepareStatement(sql,
                ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setString(i + 1, params[i]);
                }
            }

            final int update = ps.executeUpdate();
            ps.close();

            return update;

        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (final SQLException e) {
                logger.error("connection.close(), {}", e);
            }
        }
    }

    protected int update(final String sql, final String[] params, final Transaction transaction)
        throws SQLException {

        logger.info("  {}", sql);
        final Connection connection = transaction.connection;

        final PreparedStatement ps = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
            ResultSet.CONCUR_READ_ONLY);

        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
        }

        final int update = ps.executeUpdate();
        ps.close();

        return update;

    }

    protected boolean execute(final String sql) throws SQLException {
        Connection connection = null;
        try {
            logger.info(sql);
            connection = getAutoCommitConnection();

            final Statement st = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                ResultSet.CONCUR_READ_ONLY);
            final boolean execute = st.execute(sql);

            st.close();
            return execute;

        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (final SQLException e) {
                logger.error("connection.close(), {}", e);
            }
        }
    }

    protected boolean execute(final String sql, final Transaction transaction) throws SQLException {
        logger.info("  {}", sql);
        final Connection connection = transaction.connection;

        final Statement st = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
            ResultSet.CONCUR_READ_ONLY);
        final boolean execute = st.execute(sql);
        st.close();

        return execute;

    }

    protected Connection getAutoCommitConnection() throws SQLException {
        final Connection connection = DriverManager.getConnection(url);
        connection.setAutoCommit(true);
        return connection;
    }

    protected Transaction beginTransaction() throws SQLException {
        final Connection connection = getAutoCommitConnection();
        logger.info("begin transaction {}", connection.toString());

        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        final Transaction transaction = new Transaction(connection);
        return transaction;
    }

    protected class Transaction {
        final Connection connection;

        Transaction(final Connection connection) {
            this.connection = connection;
        }

        protected void commit() throws SQLException {
            try {
                logger.info("commit transaction {}", connection.toString());
                connection.commit();
            } finally {
                try {
                    connection.close();
                } catch (final SQLException e) {
                    logger.error("connection.close(), {}", e);
                }
            }
        }

        protected void rollback() throws SQLException {
            try {
                logger.info("rollback transaction {}", connection.toString());
                connection.rollback();
            } finally {
                try {
                    connection.close();
                } catch (final SQLException e) {
                    logger.error("connection.close(), {}", e);
                }
            }
        }
    }

}
