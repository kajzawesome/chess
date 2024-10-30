package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.sql.SQLException;

public class AuthDataAccessSQL {

    public AuthDataAccessSQL() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    public AuthData login(UserData user) {
        return null;
    }

    public void logout(String authToken) {

    }

    public UserData getUser(String auth) {
        return null;
    }

    public boolean validateAuth(String auth) {
        return true;
    }

    public void clearAllAuths() {

    }

    public int loggedInUsers() {
        return 0;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auths (
              AuthToken String NOT NULL,
              User UserData NOT NULL,
              PRIMARY KEY (AuthToken)
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
