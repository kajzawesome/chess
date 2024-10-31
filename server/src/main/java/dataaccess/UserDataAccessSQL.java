package dataaccess;

import exception.ResponseException;
import model.UserData;

import java.sql.SQLException;

public class UserDataAccessSQL {

    public UserDataAccessSQL() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    public UserData getUser(String username) {
        return null;
    }

    public void addNewUser(UserData user) {

    }

    public boolean alreadyRegistered(String username) {
        return true;
    }

    public void clearAllUsers() {

    }

    public int numUsers() {
        return 0;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  Users (
              'Username' String NOT NULL,
              'Password' String NOT NULL,
              'Email' String,
              'User' json TEXT DEFAULT NULL,
              PRIMARY KEY (Username),
              INDEX(Password)
            )
            """
    };

    private void configureDatabase() throws ResponseException, DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}

