package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    /*
     * Load the database information. Environment variables (DB_HOST, DB_PORT, DB_NAME,
     * DB_USER, DB_PASSWORD) win; anything not set falls back to db.properties.
     * Docker uses the environment variables; local runs use db.properties.
     */
    static {
        try {
            Properties props = new Properties();
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream != null) {
                    props.load(propStream);
                }
            }
            DATABASE_NAME = setting(props, "DB_NAME", "db.name");
            USER = setting(props, "DB_USER", "db.user");
            PASSWORD = setting(props, "DB_PASSWORD", "db.password");

            var host = setting(props, "DB_HOST", "db.host");
            var port = Integer.parseInt(setting(props, "DB_PORT", "db.port"));
            CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
        } catch (Exception ex) {
            throw new RuntimeException("unable to load database settings. " + ex.getMessage());
        }
    }

    private static String setting(Properties props, String envName, String propName) throws Exception {
        var value = System.getenv(envName);
        if (value == null || value.isEmpty()) {
            value = props.getProperty(propName);
        }
        if (value == null) {
            throw new Exception("set " + envName + " or " + propName + " in db.properties");
        }
        return value;
    }

    /**
     * Creates the database if it does not already exist.
     */
    static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */

    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
