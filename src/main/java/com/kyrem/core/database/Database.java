package com.kyrem.core.database;

import com.kyrem.core.util.Util;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private final String host;
    private final String port;
    private final String dbName;
    private final String user;
    private final String password;

    private HikariDataSource dataSource;

    public Database(String host, String port, String dbName, String user, String password) {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.user = user;
        this.password = password;
        connect();
    }

    public void connect() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + dbName);
        config.setUsername(user);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setPoolName("KyremPool");
        config.setConnectionTestQuery("SELECT 1");
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setKeepaliveTime(300000);
        config.setLeakDetectionThreshold(15000);
        config.setInitializationFailTimeout(5000);

        this.dataSource = new HikariDataSource(config);
    }

    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            Util.log("[Database] HikariCP pool closed.", true);
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Executes a DML statement (INSERT, UPDATE, DELETE, CREATE TABLE, etc.).
     *
     * @return the number of rows affected
     */
    public int executeUpdate(String sql) throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(sql);
        }
    }

    /**
     * Executes a DML statement with parameters.
     *
     * @return the number of rows affected
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeUpdate();
        }
    }

    /**
     * Executes a SELECT query and passes the ResultSet to the given handler.
     * The ResultSet and connection are closed automatically after the handler returns.
     *
     * @param sql     the SQL query
     * @param handler a callback that receives the ResultSet and returns a result
     * @return the value returned by the handler
     */
    public <T> T executeQuery(String sql, ResultSetConsumer<T> handler) throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return handler.handle(rs);
        }
    }

    /**
     * Executes a SELECT query with parameters and passes the ResultSet to the given handler.
     */
    public <T> T executeQuery(String sql, ResultSetConsumer<T> handler, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return handler.handle(rs);
            }
        }
    }
}
