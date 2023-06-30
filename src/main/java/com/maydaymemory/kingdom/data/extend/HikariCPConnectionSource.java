package com.maydaymemory.kingdom.data.extend;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.jdbc.JdbcDatabaseConnection;
import com.j256.ormlite.jdbc.db.DatabaseTypeUtils;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.misc.IOUtils;
import com.j256.ormlite.support.BaseConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HikariCPConnectionSource extends BaseConnectionSource implements ConnectionSource {
    private static Logger logger = LoggerFactory.getLogger(HikariCPConnectionSource.class);
    private HikariDataSource dataSource;
    private String databaseUrl;

    private boolean initialized;
    private DatabaseType databaseType;
    private boolean isSingleConnection;

    public HikariCPConnectionSource(HikariDataSource dataSource, String databaseUrl) throws SQLException {
        this.dataSource = dataSource;
        this.databaseUrl = databaseUrl;
        initialize();
    }

    public void initialize() throws SQLException {
        if (initialized) {
            return;
        }
        if (dataSource == null) {
            throw new IllegalStateException("dataSource was never set on " + getClass().getSimpleName());
        }
        if (databaseType == null) {
            if (databaseUrl == null) {
                throw new IllegalStateException(
                        "either the databaseUri or the databaseType must be set on " + getClass().getSimpleName());
            }
            databaseType = DatabaseTypeUtils.createDatabaseType(databaseUrl);
        }
        databaseType.loadDriver();
        if (databaseUrl != null) {
            databaseType.setDriver(DriverManager.getDriver(databaseUrl));
        }

        // see if we have a single connection data-source
        DatabaseConnection jdbcConn1 = null;
        DatabaseConnection jdbcConn2 = null;
        try {
            Connection conn1 = dataSource.getConnection();
            Connection conn2 = dataSource.getConnection();
            if (conn1 == null || conn2 == null) {
                isSingleConnection = true;
            } else {
                jdbcConn1 = new JdbcDatabaseConnection(conn1);
                jdbcConn2 = new JdbcDatabaseConnection(conn2);
                isSingleConnection = isSingleConnection(jdbcConn1, jdbcConn2);
            }
        } finally {
            IOUtils.closeQuietly(jdbcConn1);
            IOUtils.closeQuietly(jdbcConn2);
        }

        initialized = true;
    }


    @Override
    public DatabaseConnection getReadOnlyConnection(String tableName) throws SQLException {
        if (!initialized) {
            throw new SQLException(getClass().getSimpleName() + ".initialize() was not called");
        }
        return getReadWriteConnection(tableName);
    }

    public DatabaseConnection getReadOnlyConnection(String tableName, String username, String password)
            throws SQLException {
        if (!initialized) {
            throw new SQLException(getClass().getSimpleName() + ".initialize() was not called");
        }
        return getReadWriteConnection(tableName, username, password);
    }

    @Override
    public DatabaseConnection getReadWriteConnection(String tableName) throws SQLException {
        if (!initialized) {
            throw new SQLException(getClass().getSimpleName() + ".initialize() was not called");
        }
        DatabaseConnection saved = getSavedConnection();
        if (saved != null) {
            return saved;
        }
        return new JdbcDatabaseConnection(dataSource.getConnection());
    }

    @Override
    public void releaseConnection(DatabaseConnection connection) throws SQLException {
        if (!initialized) {
            throw new SQLException(getClass().getSimpleName() + ".initialize() was not called");
        }
        if (isSavedConnection(connection)) {
            // ignore the release because we will close it at the end of the connection
        } else {
            IOUtils.closeThrowSqlException(connection, "SQL connection");
        }
    }

    public DatabaseConnection getReadWriteConnection(String tableName, String username, String password)
            throws SQLException {
        if (!initialized) {
            throw new SQLException(getClass().getSimpleName() + ".initialize() was not called");
        }
        DatabaseConnection saved = getSavedConnection();
        if (saved != null) {
            return saved;
        }
        return new JdbcDatabaseConnection(dataSource.getConnection(username, password));
    }

    @Override
    public boolean saveSpecialConnection(DatabaseConnection connection) throws SQLException {
        return saveSpecial(connection);
    }

    @Override
    public void clearSpecialConnection(DatabaseConnection connection) {
        clearSpecial(connection, logger);
    }

    @Override
    public void close(){
        dataSource.close();
    }

    @Override
    public void closeQuietly() {
        IOUtils.closeQuietly(this);
    }

    @Override
    public DatabaseType getDatabaseType() {
        if (!initialized) {
            throw new IllegalStateException(getClass().getSimpleName() + ".initialize() was not called");
        }
        return databaseType;
    }

    /**
     * Unfortunately we cannot tell if the related data source has been closed so this just returns true.
     */
    @Override
    public boolean isOpen(String tableName) {
        return true;
    }

    @Override
    public boolean isSingleConnection(String tableName) {
        return isSingleConnection;
    }
}
